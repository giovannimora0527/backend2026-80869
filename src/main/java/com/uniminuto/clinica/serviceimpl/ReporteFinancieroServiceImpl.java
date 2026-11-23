package com.uniminuto.clinica.serviceimpl;

import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class ReporteFinancieroServiceImpl {

    private final JdbcTemplate jdbc;
    private final TarifaServiceImpl tarifas;

    public ReporteFinancieroServiceImpl(JdbcTemplate jdbc, TarifaServiceImpl tarifas) {
        this.jdbc = jdbc;
        this.tarifas = tarifas;
    }

    public record IngresoDiario(
            LocalDate fecha,
            long pagosRecibidos,
            long facturasConPago,
            BigDecimal ingresoRecibido
    ) {}

    @Transactional(readOnly = true)
    public List<IngresoDiario> consultarIngresos(
            String autorizacion, LocalDate desde, LocalDate hasta
    ) {
        tarifas.obtenerAdministrador(autorizacion);

        if (desde == null || hasta == null || hasta.isBefore(desde)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Selecciona un rango de fechas válido."
            );
        }

        return jdbc.query("""
                SELECT DATE(p.fecha_pago) AS fecha,
                       COUNT(*) AS pagos_recibidos,
                       COUNT(DISTINCT p.factura_id) AS facturas_con_pago,
                       SUM(p.monto) AS ingreso_recibido
                FROM factura_pago p
                WHERE p.fecha_pago >= ?
                  AND p.fecha_pago < DATE_ADD(?, INTERVAL 1 DAY)
                GROUP BY DATE(p.fecha_pago)
                ORDER BY fecha
                """, (rs, fila) -> new IngresoDiario(
                rs.getDate("fecha").toLocalDate(),
                rs.getLong("pagos_recibidos"),
                rs.getLong("facturas_con_pago"),
                rs.getBigDecimal("ingreso_recibido")
        ), desde, hasta);
    }
}