package com.uniminuto.clinica.serviceimpl;

import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;

@Service
public class FacturaPagoServiceImpl {

    private final JdbcTemplate jdbc;
    private final TarifaServiceImpl tarifaService;

    public FacturaPagoServiceImpl(JdbcTemplate jdbc, TarifaServiceImpl tarifaService) {
        this.jdbc = jdbc;
        this.tarifaService = tarifaService;
    }

    public record PagoRq(
            BigDecimal monto,
            String medioPago,
            String referencia,
            UUID claveOperacion
    ) {}

    public record ResultadoPago(
            long pagoId,
            long facturaId,
            BigDecimal monto,
            BigDecimal totalPagado,
            BigDecimal saldo,
            String estadoPago
    ) {}

    private record Factura(BigDecimal total, String estado) {}

    private record PagoPrevio(
            long id,
            long facturaId,
            long usuarioId,
            BigDecimal monto,
            String medio,
            String referencia
    ) {}

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public ResultadoPago registrar(String autorizacion, long facturaId, PagoRq datos) {
        long usuarioId = tarifaService.obtenerAdministrador(autorizacion);

        if (facturaId <= 0 || datos == null || datos.claveOperacion() == null) {
            throw error(HttpStatus.BAD_REQUEST, "Indica la factura y la clave de operación.");
        }

        if (datos.monto() == null || datos.monto().signum() <= 0) {
            throw error(HttpStatus.BAD_REQUEST, "El monto debe ser mayor que cero.");
        }

        BigDecimal monto;
        try {
            monto = datos.monto().setScale(2, RoundingMode.UNNECESSARY);
        } catch (ArithmeticException ex) {
            throw error(HttpStatus.BAD_REQUEST, "El monto admite máximo dos decimales.");
        }

        String medio = datos.medioPago() == null ? "" : datos.medioPago().trim();
        String referencia = datos.referencia() == null ? "" : datos.referencia().trim();

        if (medio.isEmpty() || medio.length() > 40 || referencia.length() > 100) {
            throw error(HttpStatus.BAD_REQUEST,
                    "Indica un medio de pago de hasta 40 caracteres y una referencia de hasta 100.");
        }

        // Dos pagos sobre la misma factura deben revisar el saldo por turno.
        List<Factura> facturas = jdbc.query("""
                SELECT total, estado_pago
                FROM factura
                WHERE id = ?
                FOR UPDATE
                """, (rs, fila) -> new Factura(
                rs.getBigDecimal("total"), rs.getString("estado_pago")
        ), facturaId);

        if (facturas.isEmpty()) {
            throw error(HttpStatus.NOT_FOUND, "La factura no existe.");
        }

        Factura factura = facturas.get(0);
        String clave = datos.claveOperacion().toString();

        List<PagoPrevio> previos = jdbc.query("""
                SELECT id, factura_id, usuario_registro_id, monto, medio_pago,
                       COALESCE(referencia, '') AS referencia
                FROM factura_pago
                WHERE clave_operacion = ?
                """, (rs, fila) -> new PagoPrevio(
                rs.getLong("id"), rs.getLong("factura_id"),
                rs.getLong("usuario_registro_id"), rs.getBigDecimal("monto"),
                rs.getString("medio_pago"), rs.getString("referencia")
        ), clave);

        BigDecimal pagado = jdbc.queryForObject("""
                SELECT COALESCE(SUM(monto), 0.00)
                FROM factura_pago
                WHERE factura_id = ?
                """, BigDecimal.class, facturaId);

        BigDecimal saldo = factura.total().subtract(pagado);

        if (!previos.isEmpty()) {
            PagoPrevio previo = previos.get(0);
            if (previo.facturaId() != facturaId || previo.usuarioId() != usuarioId
                    || previo.monto().compareTo(monto) != 0
                    || !previo.medio().equals(medio)
                    || !previo.referencia().equals(referencia)) {
                throw error(HttpStatus.CONFLICT, "La clave ya corresponde a otro pago.");
            }
            return new ResultadoPago(previo.id(), facturaId, monto, pagado, saldo, factura.estado());
        }

        if ("ANULADA".equals(factura.estado()) || "PAGADA".equals(factura.estado())) {
            throw error(HttpStatus.CONFLICT, "La factura está " + factura.estado() + ".");
        }

        if (monto.compareTo(saldo) > 0) {
            throw error(HttpStatus.CONFLICT, "El pago supera el saldo de " + saldo + ".");
        }

        jdbc.update("""
                INSERT INTO factura_pago
                    (factura_id, usuario_registro_id, monto, medio_pago, referencia, clave_operacion)
                VALUES (?, ?, ?, ?, NULLIF(?, ''), ?)
                """, facturaId, usuarioId, monto, medio, referencia, clave);

        long pagoId = jdbc.queryForObject("SELECT LAST_INSERT_ID()", Long.class);
        BigDecimal nuevoSaldo = saldo.subtract(monto);
        String nuevoEstado = nuevoSaldo.signum() == 0 ? "PAGADA" : "PAGO_PARCIAL";

        jdbc.update("UPDATE factura SET estado_pago = ? WHERE id = ?", nuevoEstado, facturaId);

        return new ResultadoPago(pagoId, facturaId, monto, pagado.add(monto), nuevoSaldo, nuevoEstado);
    }
    public record AnulacionRq(String motivo) {}

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void anular(String autorizacion, long facturaId, AnulacionRq datos) {
        long usuarioId = tarifaService.obtenerAdministrador(autorizacion);

        if (facturaId <= 0) {
            throw error(HttpStatus.BAD_REQUEST, "Indica un número de factura válido.");
        }

        String motivo = datos == null || datos.motivo() == null
                ? "" : datos.motivo().strip();

        if (motivo.isEmpty() || motivo.length() > 500) {
            throw error(HttpStatus.BAD_REQUEST,
                    "Escribe un motivo de anulación de hasta 500 caracteres.");
        }

        // El registro de pagos bloquea esta misma fila antes de modificar la factura.
        List<String> estados = jdbc.queryForList("""
            SELECT estado_pago
            FROM factura
            WHERE id = ?
            FOR UPDATE
            """, String.class, facturaId);

        if (estados.isEmpty()) {
            throw error(HttpStatus.NOT_FOUND, "La factura no existe.");
        }

        String estadoAnterior = estados.get(0);

        if ("ANULADA".equals(estadoAnterior)) {
            throw error(HttpStatus.CONFLICT, "La factura ya está anulada.");
        }

        if (!"PENDIENTE_DE_PAGO".equals(estadoAnterior)) {
            throw error(HttpStatus.CONFLICT,
                    "Solo se puede anular directamente una factura pendiente y sin pagos.");
        }

        Long cantidadPagos = jdbc.queryForObject("""
            SELECT COUNT(*)
            FROM factura_pago
            WHERE factura_id = ?
            """, Long.class, facturaId);

        if (cantidadPagos > 0) {
            throw error(HttpStatus.CONFLICT,
                    "La factura tiene pagos registrados y no admite anulación directa.");
        }

        jdbc.update("""
            INSERT INTO factura_anulacion
                (factura_id, usuario_anulacion_id, estado_pago_anterior, motivo)
            VALUES (?, ?, ?, ?)
            """, facturaId, usuarioId, estadoAnterior, motivo);

        jdbc.update("""
            UPDATE factura
            SET estado_pago = 'ANULADA'
            WHERE id = ?
            """, facturaId);
    }

    private ResponseStatusException error(HttpStatus estado, String mensaje) {
        return new ResponseStatusException(estado, mensaje);
    }
}