package com.uniminuto.clinica.serviceimpl;

import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class FacturaServiceImpl {

    private final JdbcTemplate jdbc;
    private final ServiciosAtencionService servicios;
    private final TarifaServiceImpl tarifas;

    public FacturaServiceImpl(JdbcTemplate jdbc, ServiciosAtencionService servicios, TarifaServiceImpl tarifas) {
        this.jdbc = jdbc;
        this.servicios = servicios;
        this.tarifas = tarifas;
    }

    public record FacturaEmitida(
            long facturaId,
            long citaId,
            String estadoPago,
            int numeroLineas,
            BigDecimal subtotal,
            BigDecimal impuestos,
            BigDecimal total
    ) {}

    private record Cita(
            long id,
            String estado,
            LocalDate fecha,
            long especialidadId
    ) {}

    private record Tarifa(
            long vigenciaId,
            String servicio,
            BigDecimal valor,
            BigDecimal iva
    ) {}

    private record Entrega(
            long detalleId,
            String medicamento,
            long cantidad,
            BigDecimal precio
    ) {}

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public FacturaEmitida emitir(String autorizacion, long citaId, Long servicioId) {
        long usuarioId = tarifas.obtenerAdministrador(autorizacion);

        if (citaId <= 0 || (servicioId != null && servicioId <= 0)) {
            throw error(HttpStatus.BAD_REQUEST, "Selecciona una cita y un servicio válidos.");
        }

        // Dos solicitudes de facturación para la misma cita se procesan en orden.
        List<Cita> citas = jdbc.query("""
                SELECT c.id, c.estado, DATE(c.fecha_hora) AS fecha,
                       me.especializacion_id
                FROM cita c
                JOIN medico me ON me.id = c.medico_id
                WHERE c.id = ?
                FOR UPDATE
                """, (rs, fila) -> new Cita(
                rs.getLong("id"),
                rs.getString("estado"),
                rs.getObject("fecha", LocalDate.class),
                rs.getLong("especializacion_id")
        ), citaId);

        if (citas.isEmpty()) {
            throw error(HttpStatus.NOT_FOUND, "La cita no existe.");
        }

        Cita cita = citas.get(0);
        if (!"ATENDIDA".equalsIgnoreCase(cita.estado())) {
            throw error(HttpStatus.CONFLICT, "Solo se pueden facturar citas atendidas.");
        }

        Integer consultaCobrada = jdbc.queryForObject("""
                SELECT EXISTS (
                    SELECT 1 FROM factura_detalle WHERE cita_consulta_id = ?
                )
                """, Integer.class, citaId);

        Tarifa consulta = null;
        if (!Integer.valueOf(1).equals(consultaCobrada)) {
            consulta = buscarTarifa(cita, servicioId);
        }

        List<Entrega> entregas = jdbc.query("""
                SELECT dd.id, m.nombre, dd.cantidad_entregada, m.precio_venta
                FROM despacho_detalle dd
                JOIN formula_medica f ON f.id = dd.formula_id
                JOIN medicamento_lote l ON l.id = dd.lote_id
                JOIN medicamento m ON m.id = l.medicamento_id
                WHERE f.cita_id = ?
                  AND NOT EXISTS (
                      SELECT 1
                      FROM factura_detalle fd
                      WHERE fd.despacho_detalle_id = dd.id
                  )
                ORDER BY dd.id
                """, (rs, fila) -> new Entrega(
                rs.getLong("id"),
                rs.getString("nombre"),
                rs.getLong("cantidad_entregada"),
                rs.getBigDecimal("precio_venta")
        ), citaId);

        if (consulta == null && entregas.isEmpty()) {
            throw error(HttpStatus.CONFLICT,
                    "Esta cita ya está facturada y no tiene nuevas entregas por cobrar.");
        }

        for (Entrega entrega : entregas) {
            if (entrega.precio() == null) {
                throw error(HttpStatus.CONFLICT,
                        "Registra el precio de venta de " + entrega.medicamento()
                                + " antes de emitir la factura.");
            }
        }

        jdbc.update("""
                INSERT INTO factura
                    (cita_id, usuario_emisor_id, subtotal, descuento, impuestos, total)
                VALUES (?, ?, 0, 0, 0, 0)
                """, citaId, usuarioId);

        long facturaId = jdbc.queryForObject("SELECT LAST_INSERT_ID()", Long.class);

        if (consulta != null) {
            jdbc.update("""
                    INSERT INTO factura_detalle
                        (factura_id, cita_id, tipo, descripcion, tarifa_vigencia_id,
                         cantidad, precio_unitario, porcentaje_iva)
                    VALUES (?, ?, 'CONSULTA', ?, ?, 1, ?, ?)
                    """, facturaId, citaId, consulta.servicio(),
                    consulta.vigenciaId(), consulta.valor(), consulta.iva());
        }

        if (consulta != null) {
            for (var servicio : servicios.guardados(citaId)) {
                jdbc.update("""
                        INSERT INTO factura_detalle
                            (factura_id, cita_id, tipo, descripcion, tarifa_vigencia_id,
                             cantidad, precio_unitario, porcentaje_iva)
                        VALUES (?, ?, 'SERVICIO', ?, ?, 1, ?, ?)
                        """, facturaId, citaId, servicio.nombre(),
                        servicio.vigenciaId(), servicio.valor(), servicio.porcentajeIva());
            }
        }

        for (Entrega entrega : entregas) {
            jdbc.update("""
                    INSERT INTO factura_detalle
                        (factura_id, cita_id, tipo, descripcion, despacho_detalle_id,
                         cantidad, precio_unitario, porcentaje_iva)
                    VALUES (?, ?, 'MEDICAMENTO', ?, ?, ?, ?, 0)
                    """, facturaId, citaId, entrega.medicamento(), entrega.detalleId(),
                    entrega.cantidad(), entrega.precio());
        }

        // Los totales salen de las líneas que acabamos de guardar.
        jdbc.update("""
                UPDATE factura f
                JOIN (
                    SELECT factura_id,
                           SUM(subtotal) AS subtotal,
                           SUM(descuento) AS descuento,
                           SUM(valor_iva) AS impuestos,
                           SUM(total_linea) AS total
                    FROM factura_detalle
                    WHERE factura_id = ?
                    GROUP BY factura_id
                ) d ON d.factura_id = f.id
                SET f.subtotal = d.subtotal,
                    f.descuento = d.descuento,
                    f.impuestos = d.impuestos,
                    f.total = d.total
                WHERE f.id = ?
                """, facturaId, facturaId);

        return jdbc.queryForObject("""
                SELECT f.id, f.cita_id, f.estado_pago, f.subtotal, f.impuestos, f.total,
                       (SELECT COUNT(*) FROM factura_detalle d
                        WHERE d.factura_id = f.id) AS numero_lineas
                FROM factura f
                WHERE f.id = ?
                """, (rs, fila) -> new FacturaEmitida(
                rs.getLong("id"),
                rs.getLong("cita_id"),
                rs.getString("estado_pago"),
                rs.getInt("numero_lineas"),
                rs.getBigDecimal("subtotal"),
                rs.getBigDecimal("impuestos"),
                rs.getBigDecimal("total")
        ), facturaId);
    }
    public record LineaGuardada(
            long id,
            String descripcion,
            long cantidad,
            BigDecimal precioUnitario,
            BigDecimal subtotal,
            BigDecimal descuento,
            BigDecimal porcentajeIva,
            BigDecimal valorIva,
            BigDecimal totalLinea
    ) {}
    public record CitaFacturable(long id, String fecha, String mascota, String propietario) {}

    @Transactional(readOnly = true)
    public List<CitaFacturable> buscarCitasAtendidas(String autorizacion, String busqueda) {
        tarifas.obtenerAdministrador(autorizacion);
        String termino = busqueda == null ? "" : busqueda.strip();
        if (termino.isEmpty() || termino.length() > 120) {
            throw error(HttpStatus.BAD_REQUEST, "Indica un documento o nombre de mascota válido.");
        }

        return jdbc.query("""
                SELECT c.id, DATE_FORMAT(c.fecha_hora, '%d/%m/%Y %H:%i') AS fecha,
                       ma.nombre_mascota,
                       CONCAT_WS(' ', cl.nombres, cl.apellidos) AS propietario
                FROM cita c
                JOIN mascota ma ON ma.mascota_id = c.mascota_id
                JOIN cliente cl ON cl.cliente_id = c.cliente_id
                WHERE c.estado = 'ATENDIDA'
                  AND (cl.numero_documento = ? OR LOCATE(LOWER(?), LOWER(ma.nombre_mascota)) > 0)
                ORDER BY c.fecha_hora DESC, c.id DESC
                """, (rs, fila) -> new CitaFacturable(
                rs.getLong("id"), rs.getString("fecha"),
                rs.getString("nombre_mascota"), rs.getString("propietario")
        ), termino, termino);
    }

    public record FacturaHistorial(
            long facturaId,
            long citaId,
            String fechaEmision,
            String propietario,
            String mascota,
            String estadoPago,
            BigDecimal total,
            BigDecimal totalPagado,
            BigDecimal saldo
    ) {}

    @Transactional(readOnly = true)
    public List<FacturaHistorial> buscarHistorial(String autorizacion, String busqueda) {
        tarifas.obtenerAdministrador(autorizacion);

        String termino = busqueda == null ? "" : busqueda.strip();
        if (termino.isEmpty()) {
            throw error(HttpStatus.BAD_REQUEST, "Escribe un propietario, documento o mascota.");
        }

        return jdbc.query("""
            SELECT f.id, f.cita_id,
                   DATE_FORMAT(f.fecha_emision, '%d/%m/%Y %H:%i') AS fecha,
                   CONCAT_WS(' ', cl.nombres, cl.apellidos) AS propietario,
                   ma.nombre_mascota AS mascota,
                   f.estado_pago, f.total,
                   COALESCE((
                       SELECT SUM(p.monto)
                       FROM factura_pago p
                       WHERE p.factura_id = f.id
                   ), 0.00) AS pagado
            FROM factura f
            JOIN cita c ON c.id = f.cita_id
            JOIN cliente cl ON cl.cliente_id = c.cliente_id
            JOIN mascota ma ON ma.mascota_id = c.mascota_id
            WHERE LOCATE(?, ma.nombre_mascota) > 0
               OR LOCATE(?, CONCAT_WS(' ', cl.nombres, cl.apellidos)) > 0
               OR cl.numero_documento = ?
            ORDER BY f.fecha_emision DESC, f.id DESC
            """, (rs, fila) -> {
            BigDecimal total = rs.getBigDecimal("total");
            BigDecimal pagado = rs.getBigDecimal("pagado");
            String estado = rs.getString("estado_pago");

            return new FacturaHistorial(
                    rs.getLong("id"),
                    rs.getLong("cita_id"),
                    rs.getString("fecha"),
                    rs.getString("propietario"),
                    rs.getString("mascota"),
                    estado,
                    total,
                    pagado,
                    "ANULADA".equals(estado) ? BigDecimal.ZERO : total.subtract(pagado)
            );
        }, termino, termino, termino);
    }

    public record FacturaGuardada(
            long facturaId,
            long citaId,
            String fechaEmision,
            String propietario,
            String mascota,
            String medico,
            String especialidad,
            String estadoPago,
            BigDecimal subtotal,
            BigDecimal descuento,
            BigDecimal impuestos,
            BigDecimal total,
            BigDecimal totalPagado,
            BigDecimal saldo,
            List<LineaGuardada> lineas
    ) {}

    @Transactional(readOnly = true)
    public List<FacturaGuardada> consultarPorCita(String autorizacion, long citaId) {
        tarifas.obtenerAdministrador(autorizacion);

        if (citaId <= 0) {
            throw error(HttpStatus.BAD_REQUEST, "Indica un número de cita válido.");
        }

        List<FacturaGuardada> facturas = jdbc.query("""
                SELECT f.id, f.cita_id,
                       DATE_FORMAT(f.fecha_emision, '%d/%m/%Y %H:%i') AS fecha,
                       CONCAT_WS(' ', cl.nombres, cl.apellidos) AS propietario,
                       ma.nombre_mascota AS mascota,
                       CONCAT_WS(' ', me.nombres, me.apellidos) AS medico,
                       e.nombre AS especialidad,
                       f.estado_pago, f.subtotal, f.descuento, f.impuestos, f.total,
                                                COALESCE((
                                                    SELECT SUM(p.monto)
                                                    FROM factura_pago p
                                                    WHERE p.factura_id = f.id
                                                ), 0.00) AS total_pagado
                FROM factura f
                JOIN cita c ON c.id = f.cita_id
                JOIN cliente cl ON cl.cliente_id = c.cliente_id
                JOIN mascota ma ON ma.mascota_id = c.mascota_id
                JOIN medico me ON me.id = c.medico_id
                JOIN especializacion e ON e.id = me.especializacion_id
                WHERE f.cita_id = ?
                ORDER BY f.id DESC
                """, (rs, fila) -> new FacturaGuardada(
                rs.getLong("id"),
                rs.getLong("cita_id"),
                rs.getString("fecha"),
                rs.getString("propietario"),
                rs.getString("mascota"),
                rs.getString("medico"),
                rs.getString("especialidad"),
                rs.getString("estado_pago"),
                rs.getBigDecimal("subtotal"),
                rs.getBigDecimal("descuento"),
                rs.getBigDecimal("impuestos"),
                rs.getBigDecimal("total"),
                rs.getBigDecimal("total_pagado"),
                "ANULADA".equals(rs.getString("estado_pago"))
                        ? BigDecimal.ZERO
                        : rs.getBigDecimal("total").subtract(rs.getBigDecimal("total_pagado")),
                new java.util.ArrayList<>()
        ), citaId);

        if (facturas.isEmpty()) return facturas;

        var porId = new java.util.HashMap<Long, FacturaGuardada>();
        for (FacturaGuardada factura : facturas) {
            porId.put(factura.facturaId(), factura);
        }

        jdbc.query("""
                SELECT id, factura_id, descripcion, cantidad, precio_unitario,
                       subtotal, descuento, porcentaje_iva, valor_iva, total_linea
                FROM factura_detalle
                WHERE cita_id = ?
                ORDER BY factura_id, id
                """, (org.springframework.jdbc.core.RowCallbackHandler) rs -> {
            FacturaGuardada factura = porId.get(rs.getLong("factura_id"));
            factura.lineas().add(new LineaGuardada(
                    rs.getLong("id"),
                    rs.getString("descripcion"),
                    rs.getLong("cantidad"),
                    rs.getBigDecimal("precio_unitario"),
                    rs.getBigDecimal("subtotal"),
                    rs.getBigDecimal("descuento"),
                    rs.getBigDecimal("porcentaje_iva"),
                    rs.getBigDecimal("valor_iva"),
                    rs.getBigDecimal("total_linea")
            ));
        }, citaId);

        return facturas;
    }

    private Tarifa buscarTarifa(Cita cita, Long servicioId) {
        String sql = """
                SELECT v.id, t.nombre_servicio, v.valor_base, v.porcentaje_iva
                FROM tarifa_servicio t
                JOIN tarifa_vigencia v ON v.id = (
                    SELECT v2.id
                    FROM tarifa_vigencia v2
                    WHERE v2.tarifa_servicio_id = t.id
                      AND v2.vigente_desde <= ?
                    ORDER BY v2.vigente_desde DESC
                    LIMIT 1
                )
                WHERE t.especializacion_id = ?
                """;

        Object[] parametros = { cita.fecha(), cita.especialidadId() };
        if (servicioId != null) {
            sql += " AND t.id = ?";
            parametros = new Object[] { cita.fecha(), cita.especialidadId(), servicioId };
        } else {
            sql += " AND t.activo = 1 AND t.nombre_servicio LIKE 'Consulta %'"
                    + " ORDER BY (t.nombre_servicio = 'Consulta general') DESC, t.id";
        }

        List<Tarifa> opciones = jdbc.query(sql, (rs, fila) -> new Tarifa(
                rs.getLong("id"),
                rs.getString("nombre_servicio"),
                rs.getBigDecimal("valor_base"),
                rs.getBigDecimal("porcentaje_iva")
        ), parametros);

        if (opciones.isEmpty()) {
            throw error(HttpStatus.CONFLICT,
                    "No hay tarifa aplicable para el servicio en la fecha de atención.");
        }
        if (opciones.size() > 1 && !"Consulta general".equalsIgnoreCase(opciones.get(0).servicio())) {
            throw error(HttpStatus.CONFLICT,
                    "Hay varios servicios. Selecciona cuál se prestó.");
        }
        return opciones.get(0);
    }

    private ResponseStatusException error(HttpStatus estado, String mensaje) {
        return new ResponseStatusException(estado, mensaje);
    }
}