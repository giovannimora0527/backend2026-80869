package com.uniminuto.clinica.serviceimpl;

import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;


import java.util.List;
import java.util.UUID;

@Service
public class DespachoServiceImpl {

    private final JdbcTemplate jdbc;

    public DespachoServiceImpl(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public record EntregaRq(
            Long formulaId,
            Integer numeroLinea,
            Long loteId,
            Integer cantidad,
            UUID claveOperacion
    ) {
    }

    public record ComprobanteEntrega(
            long entregaId,
            long formulaId,
            int numeroLinea,
            long loteId,
            int cantidadEntregada
    ) {
    }

    private record LineaPrescrita(long medicamentoId, Long cantidad) {
    }

    private record Despacho(long id, String estado) {
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public ComprobanteEntrega confirmar(String autorizacion, EntregaRq datos) {
        long usuarioId = obtenerUsuarioDespacho(autorizacion);
        validarSolicitud(datos);

        // Las entregas de una misma fórmula se procesan por turno.
        List<Long> formulas = jdbc.queryForList("""
                SELECT id FROM formula_medica
                WHERE id = ?
                FOR UPDATE
                """, Long.class, datos.formulaId());

        if (formulas.isEmpty()) {
            throw error(HttpStatus.NOT_FOUND, "La fórmula seleccionada no existe.");
        }

        String clave = datos.claveOperacion().toString();

        List<ComprobanteEntrega> previas = jdbc.query("""
                SELECT e.id, d.formula_id, d.numero_linea,
                       d.lote_id, d.cantidad_entregada, e.usuario_id
                FROM despacho_entrega e
                JOIN despacho_detalle d ON d.entrega_id = e.id
                WHERE e.clave_operacion = ?
                """, (rs, fila) -> {
            if (rs.getLong("usuario_id") != usuarioId) {
                throw error(HttpStatus.CONFLICT, "La clave de operación ya está en uso.");
            }
            return new ComprobanteEntrega(
                    rs.getLong("id"),
                    rs.getLong("formula_id"),
                    rs.getInt("numero_linea"),
                    rs.getLong("lote_id"),
                    rs.getInt("cantidad_entregada")
            );
        }, clave);

        if (!previas.isEmpty()) {
            ComprobanteEntrega previa = previas.get(0);
            if (previa.formulaId() != datos.formulaId()
                    || previa.numeroLinea() != datos.numeroLinea()
                    || previa.loteId() != datos.loteId()
                    || previa.cantidadEntregada() != datos.cantidad()) {
                throw error(HttpStatus.CONFLICT,
                        "La clave ya corresponde a otra operación.");
            }
            return previa;
        }

        List<LineaPrescrita> lineas = jdbc.query("""
                SELECT medicamento_id, cantidad_prescrita
                FROM formula_medica_detalle
                WHERE formula_id = ? AND numero_linea = ?
                FOR UPDATE
                """, (rs, fila) -> new LineaPrescrita(
                rs.getLong("medicamento_id"),
                rs.getObject("cantidad_prescrita", Long.class)
        ), datos.formulaId(), datos.numeroLinea());

        if (lineas.isEmpty()) {
            throw error(HttpStatus.NOT_FOUND, "La línea seleccionada no existe.");
        }

        LineaPrescrita linea = lineas.get(0);
        if (linea.cantidad() == null) {
            throw error(HttpStatus.CONFLICT,
                    "Esta línea todavía no tiene cantidad prescrita.");
        }

        Long entregado = jdbc.queryForObject("""
                SELECT COALESCE(SUM(cantidad_entregada), 0)
                FROM despacho_detalle
                WHERE formula_id = ? AND numero_linea = ?
                """, Long.class, datos.formulaId(), datos.numeroLinea());

        long pendiente = linea.cantidad() - entregado;
        if (datos.cantidad() > pendiente) {
            throw error(HttpStatus.CONFLICT,
                    "La cantidad supera el saldo pendiente de la fórmula: " + pendiente);
        }

        List<Despacho> existentes = jdbc.query("""
                SELECT id, estado FROM despacho
                WHERE formula_id = ?
                FOR UPDATE
                """, (rs, fila) -> new Despacho(
                rs.getLong("id"), rs.getString("estado")
        ), datos.formulaId());

        Despacho despacho;

        if (existentes.isEmpty()) {
            jdbc.update("""
                    INSERT INTO despacho (formula_id, estado)
                    VALUES (?, 'FORMULADO')
                    """, datos.formulaId());

            long id = ultimoId();

            jdbc.update("""
                    INSERT INTO despacho_historial
                        (despacho_id, usuario_id, estado_anterior, estado_nuevo, motivo)
                    VALUES (?, ?, NULL, 'FORMULADO', 'Inicio del despacho')
                    """, id, usuarioId);

            despacho = new Despacho(id, "FORMULADO");
        } else {
            despacho = existentes.get(0);
        }

        if ("CANCELADO".equals(despacho.estado())
                || "DESPACHADO".equals(despacho.estado())) {
            throw error(HttpStatus.CONFLICT,
                    "El estado actual impide registrar otra entrega.");
        }

        // Se prioriza el lote vigente que vence primero.
        List<Long> primerosLotes = jdbc.queryForList("""
                SELECT id
                FROM medicamento_lote
                WHERE medicamento_id = ?
                  AND cantidad_disponible > 0
                  AND fecha_ingreso <= CURRENT_DATE
                  AND fecha_vence >= CURRENT_DATE
                ORDER BY fecha_vence, id
                LIMIT 1
                FOR UPDATE
                """, Long.class, linea.medicamentoId());

        if (primerosLotes.isEmpty()) {
            throw error(HttpStatus.CONFLICT, "El medicamento no tiene lotes disponibles.");
        }

        if (!primerosLotes.get(0).equals(datos.loteId())) {
            throw error(HttpStatus.CONFLICT,
                    "Actualiza los lotes. Debes usar primero el lote vigente con vencimiento más próximo.");
        }

        int descontados = jdbc.update("""
                UPDATE medicamento_lote
                SET cantidad_disponible = cantidad_disponible - ?
                WHERE id = ?
                  AND medicamento_id = ?
                  AND cantidad_disponible >= ?
                  AND fecha_ingreso <= CURRENT_DATE
                  AND fecha_vence >= CURRENT_DATE
                """, datos.cantidad(), datos.loteId(),
                linea.medicamentoId(), datos.cantidad());

        if (descontados != 1) {
            throw error(HttpStatus.CONFLICT,
                    "El lote no tiene suficientes unidades. Revisa la cantidad a entregar.");
        }

        jdbc.update("""
                INSERT INTO despacho_entrega
                    (despacho_id, usuario_id, clave_operacion)
                VALUES (?, ?, ?)
                """, despacho.id(), usuarioId, clave);

        long entregaId = ultimoId();

        jdbc.update("""
                INSERT INTO despacho_detalle
                    (entrega_id, formula_id, numero_linea, lote_id, cantidad_entregada)
                VALUES (?, ?, ?, ?, ?)
                """, entregaId, datos.formulaId(), datos.numeroLinea(),
                datos.loteId(), datos.cantidad());

        Long lineasPendientes = jdbc.queryForObject("""
                SELECT COUNT(*)
                FROM formula_medica_detalle f
                WHERE f.formula_id = ?
                  AND (
                    f.cantidad_prescrita IS NULL OR
                    f.cantidad_prescrita > (
                        SELECT COALESCE(SUM(d.cantidad_entregada), 0)
                        FROM despacho_detalle d
                        WHERE d.formula_id = f.formula_id
                          AND d.numero_linea = f.numero_linea
                    )
                  )
                """, Long.class, datos.formulaId());

        String nuevoEstado = lineasPendientes == 0
                ? "DESPACHADO" : "DESPACHO_PARCIAL";

        if (!nuevoEstado.equals(despacho.estado())) {
            jdbc.update("UPDATE despacho SET estado = ? WHERE id = ?",
                    nuevoEstado, despacho.id());

            jdbc.update("""
                    INSERT INTO despacho_historial
                        (despacho_id, usuario_id, estado_anterior, estado_nuevo, motivo)
                    VALUES (?, ?, ?, ?, ?)
                    """, despacho.id(), usuarioId, despacho.estado(),
                    nuevoEstado, "Confirmación de entrega " + entregaId);
        }

        return new ComprobanteEntrega(
                entregaId, datos.formulaId(), datos.numeroLinea(),
                datos.loteId(), datos.cantidad()
        );
    }
    public record LineaPendiente(
            long formulaId,
            int numeroLinea,
            String medicamento,
            Long cantidadPrescrita,
            long cantidadEntregada,
            Long loteId,
            String numeroLote,
            long disponibleLote,
            String fechaVence,
            String estado,
            String dosis,
            String viaAdministracion,
            String frecuencia,
            String duracionTratamiento,
            String indicaciones
    ) {
    }

    @Transactional(readOnly = true)
    public List<LineaPendiente> consultarFormula(String autorizacion, Long formulaId) {
        obtenerUsuarioDespacho(autorizacion);

        if (formulaId == null || formulaId <= 0) {
            throw error(HttpStatus.BAD_REQUEST, "Indica un número de fórmula válido.");
        }

        List<LineaPendiente> resultado = jdbc.query("""
            SELECT f.formula_id,
                   f.numero_linea,
                   m.nombre AS medicamento,
                   f.cantidad_prescrita,
                   f.dosis,
                   f.via_administracion,
                   f.frecuencia,
                   f.duracion_tratamiento,
                   f.indicaciones,
                   COALESCE((
                       SELECT SUM(dd.cantidad_entregada)
                       FROM despacho_detalle dd
                       WHERE dd.formula_id = f.formula_id
                         AND dd.numero_linea = f.numero_linea
                   ), 0) AS cantidad_entregada,
                   l.id AS lote_id,
                   l.numero_lote,
                   COALESCE(l.cantidad_disponible, 0) AS disponible_lote,
                   l.fecha_vence,
                   COALESCE(d.estado, 'FORMULADO') AS estado
            FROM formula_medica_detalle f
            JOIN medicamento m ON m.id = f.medicamento_id
            LEFT JOIN despacho d ON d.formula_id = f.formula_id
            LEFT JOIN medicamento_lote l ON l.id = (
                SELECT ml.id
                FROM medicamento_lote ml
                WHERE ml.medicamento_id = f.medicamento_id
                  AND ml.cantidad_disponible > 0
                  AND ml.fecha_ingreso <= CURRENT_DATE
                  AND ml.fecha_vence >= CURRENT_DATE
                ORDER BY ml.fecha_vence, ml.id
                LIMIT 1
            )
            WHERE f.formula_id = ?
            ORDER BY f.numero_linea
            """, (rs, fila) -> new LineaPendiente(
                rs.getLong("formula_id"),
                rs.getInt("numero_linea"),
                rs.getString("medicamento"),
                rs.getObject("cantidad_prescrita", Long.class),
                rs.getLong("cantidad_entregada"),
                rs.getObject("lote_id", Long.class),
                rs.getString("numero_lote"),
                rs.getLong("disponible_lote"),
                rs.getString("fecha_vence"),
                rs.getString("estado"),
                rs.getString("dosis"),
                rs.getString("via_administracion"),
                rs.getString("frecuencia"),
                rs.getString("duracion_tratamiento"),
                rs.getString("indicaciones")
        ), formulaId);

        if (resultado.isEmpty()) {
            throw error(HttpStatus.NOT_FOUND,
                    "La fórmula no existe o todavía no tiene medicamentos registrados.");
        }

        return resultado;
    }
    public record FilaComprobante(
            long entregaId,
            long formulaId,
            String fechaEntrega,
            String responsable,
            String propietario,
            String mascota,
            int numeroLinea,
            String medicamento,
            String numeroLote,
            long cantidadEntregada
    ) {
    }

    @Transactional(readOnly = true)
    public List<FilaComprobante> listarComprobantes(
            String autorizacion,
            Long formulaId
    ) {
        obtenerUsuarioDespacho(autorizacion);

        if (formulaId == null || formulaId <= 0) {
            throw error(HttpStatus.BAD_REQUEST, "Indica un número de fórmula válido.");
        }

        return jdbc.query("""
            SELECT e.id AS entrega_id,
                   d.formula_id,
                   DATE_FORMAT(e.fecha_entrega, '%d/%m/%Y %H:%i:%s') AS fecha_entrega,
                   u.username AS responsable,
                   CONCAT_WS(' ', cl.nombres, cl.apellidos) AS propietario,
                   ma.nombre_mascota AS mascota,
                   dd.numero_linea,
                   m.nombre AS medicamento,
                   l.numero_lote,
                   dd.cantidad_entregada
            FROM despacho_entrega e
            JOIN despacho d ON d.id = e.despacho_id
            JOIN despacho_detalle dd ON dd.entrega_id = e.id
            JOIN medicamento_lote l ON l.id = dd.lote_id
            JOIN medicamento m ON m.id = l.medicamento_id
            JOIN usuario u ON u.id = e.usuario_id
            JOIN formula_medica f ON f.id = d.formula_id
            JOIN cita c ON c.id = f.cita_id
            JOIN cliente cl ON cl.cliente_id = c.cliente_id
            JOIN mascota ma ON ma.mascota_id = c.mascota_id
            WHERE d.formula_id = ?
            ORDER BY e.fecha_entrega DESC, e.id DESC, dd.numero_linea, dd.id
            """, (rs, fila) -> new FilaComprobante(
                rs.getLong("entrega_id"),
                rs.getLong("formula_id"),
                rs.getString("fecha_entrega"),
                rs.getString("responsable"),
                rs.getString("propietario"),
                rs.getString("mascota"),
                rs.getInt("numero_linea"),
                rs.getString("medicamento"),
                rs.getString("numero_lote"),
                rs.getLong("cantidad_entregada")
        ), formulaId);
    }
    public record FormulaEncontrada(
            long formulaId,
            String fechaGeneracion,
            String propietario,
            String mascota,
            String medico,
            String estado
    ) {
    }

    @Transactional(readOnly = true)
    public List<FormulaEncontrada> buscarFormulas(
            String autorizacion,
            String texto,
            java.time.LocalDate fecha
    ) {
        obtenerUsuarioDespacho(autorizacion);

        String termino = texto == null ? "" : texto.strip();
        if (termino.isEmpty() && fecha == null) {
            throw error(HttpStatus.BAD_REQUEST,
                    "Escribe un documento, un nombre o selecciona una fecha.");
        }

        StringBuilder sql = new StringBuilder("""
                SELECT f.id AS formula_id,
                       COALESCE(f.fecha_emision, DATE(f.fecha_creacion_registro)) AS fecha_generacion,
                       CONCAT_WS(' ', cl.nombres, cl.apellidos) AS propietario,
                       ma.nombre_mascota AS mascota,
                       CONCAT_WS(' ', me.nombres, me.apellidos) AS medico,
                       COALESCE(d.estado, 'FORMULADO') AS estado
                FROM formula_medica f
                JOIN cita c ON c.id = f.cita_id
                JOIN cliente cl ON cl.cliente_id = c.cliente_id
                JOIN mascota ma ON ma.mascota_id = c.mascota_id
                JOIN medico me ON me.id = c.medico_id
                LEFT JOIN despacho d ON d.formula_id = f.id
                WHERE COALESCE(d.estado, 'FORMULADO') NOT IN ('DESPACHADO', 'CANCELADO')
                  AND EXISTS (
                      SELECT 1
                      FROM formula_medica_detalle fd
                      WHERE fd.formula_id = f.id
                  )
                """);

        List<Object> parametros = new java.util.ArrayList<>();

        if (!termino.isEmpty()) {
            sql.append("""
                      AND (
                          cl.numero_documento = ?
                          OR LOCATE(LOWER(?), LOWER(CONCAT_WS(' ', cl.nombres, cl.apellidos))) > 0
                          OR LOCATE(LOWER(?), LOWER(ma.nombre_mascota)) > 0
                      )
                    """);
            parametros.add(termino);
            parametros.add(termino);
            parametros.add(termino);
        }

        if (fecha != null) {
            sql.append("""
                      AND COALESCE(f.fecha_emision, DATE(f.fecha_creacion_registro)) = ?
                    """);
            parametros.add(java.sql.Date.valueOf(fecha));
        }

        sql.append(" ORDER BY fecha_generacion DESC, f.id DESC");

        return jdbc.query(sql.toString(), (rs, fila) -> new FormulaEncontrada(
                rs.getLong("formula_id"),
                rs.getString("fecha_generacion"),
                rs.getString("propietario"),
                rs.getString("mascota"),
                rs.getString("medico"),
                rs.getString("estado")
        ), parametros.toArray());
    }
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void iniciarPreparacion(String autorizacion, Long formulaId) {
        long usuarioId = obtenerUsuarioDespacho(autorizacion);

        if (formulaId == null || formulaId <= 0) {
            throw error(HttpStatus.BAD_REQUEST, "Indica un número de fórmula válido.");
        }

        // Confirmar entrega también bloquea primero la fórmula.
        // Así ambas operaciones procesan sus cambios en el mismo orden.
        List<Long> formulas = jdbc.queryForList("""
                SELECT id
                FROM formula_medica
                WHERE id = ?
                FOR UPDATE
                """, Long.class, formulaId);

        if (formulas.isEmpty()) {
            throw error(HttpStatus.NOT_FOUND, "La fórmula seleccionada no existe.");
        }

        List<Despacho> despachos = jdbc.query("""
                SELECT id, estado
                FROM despacho
                WHERE formula_id = ?
                FOR UPDATE
                """, (rs, fila) -> new Despacho(
                rs.getLong("id"),
                rs.getString("estado")
        ), formulaId);

        if (!despachos.isEmpty()) {
            String estado = despachos.get(0).estado();

            if ("EN_PREPARACION".equals(estado)) {
                return;
            }

            if (!"FORMULADO".equals(estado)) {
                throw error(HttpStatus.CONFLICT,
                        "La fórmula está en estado " + estado
                                + " y ya no puede iniciar preparación.");
            }
        }

        Integer tieneDetalle = jdbc.queryForObject("""
                SELECT EXISTS (
                    SELECT 1
                    FROM formula_medica_detalle
                    WHERE formula_id = ?
                )
                """, Integer.class, formulaId);

        if (!Integer.valueOf(1).equals(tieneDetalle)) {
            throw error(HttpStatus.CONFLICT,
                    "La fórmula todavía no tiene medicamentos registrados.");
        }

        long despachoId;

        if (despachos.isEmpty()) {
            jdbc.update("""
                    INSERT INTO despacho (formula_id, estado)
                    VALUES (?, 'EN_PREPARACION')
                    """, formulaId);
            despachoId = ultimoId();
        } else {
            despachoId = despachos.get(0).id();
            jdbc.update("""
                    UPDATE despacho
                    SET estado = 'EN_PREPARACION'
                    WHERE id = ?
                    """, despachoId);
        }

        jdbc.update("""
                INSERT INTO despacho_historial
                    (despacho_id, usuario_id, estado_anterior, estado_nuevo, motivo)
                VALUES (?, ?, 'FORMULADO', 'EN_PREPARACION', ?)
                """, despachoId, usuarioId, "Inicio de preparación de la fórmula");
    }
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void marcarPendientePorDisponibilidad(String autorizacion, Long formulaId) {
        long usuarioId = obtenerUsuarioDespacho(autorizacion);

        if (formulaId == null || formulaId <= 0) {
            throw error(HttpStatus.BAD_REQUEST, "Indica un número de fórmula válido.");
        }

        // Usamos el mismo orden de bloqueo que en preparación y entrega.
        List<Long> formulas = jdbc.queryForList("""
                SELECT id
                FROM formula_medica
                WHERE id = ?
                FOR UPDATE
                """, Long.class, formulaId);

        if (formulas.isEmpty()) {
            throw error(HttpStatus.NOT_FOUND, "La fórmula seleccionada no existe.");
        }

        List<Despacho> despachos = jdbc.query("""
                SELECT id, estado
                FROM despacho
                WHERE formula_id = ?
                FOR UPDATE
                """, (rs, fila) -> new Despacho(
                rs.getLong("id"),
                rs.getString("estado")
        ), formulaId);

        if (despachos.isEmpty()) {
            throw error(HttpStatus.CONFLICT, "Primero inicia la preparación de la fórmula.");
        }

        Despacho despacho = despachos.get(0);
        String estadoAnterior = despacho.estado();
        String nuevoEstado = "PENDIENTE_DISPONIBILIDAD";

        if (nuevoEstado.equals(estadoAnterior)) {
            return;
        }

        if (!"EN_PREPARACION".equals(estadoAnterior)
                && !"DESPACHO_PARCIAL".equals(estadoAnterior)) {
            throw error(HttpStatus.CONFLICT,
                    "El estado actual no permite marcar un faltante de inventario.");
        }

        // Restamos lo entregado en cada línea y luego sumamos por medicamento.
        // Así, dos líneas del mismo producto comparten las mismas existencias.
        List<String> faltantes = jdbc.queryForList("""
                WITH entregas AS (
                    SELECT numero_linea, SUM(cantidad_entregada) AS cantidad
                    FROM despacho_detalle
                    WHERE formula_id = ?
                    GROUP BY numero_linea
                ),
                pendientes AS (
                    SELECT fd.medicamento_id,
                           SUM(GREATEST(
                               CAST(fd.cantidad_prescrita AS SIGNED)
                               - COALESCE(e.cantidad, 0), 0
                           )) AS cantidad
                    FROM formula_medica_detalle fd
                    LEFT JOIN entregas e ON e.numero_linea = fd.numero_linea
                    WHERE fd.formula_id = ?
                      AND fd.cantidad_prescrita IS NOT NULL
                    GROUP BY fd.medicamento_id
                ),
                existencias AS (
                    SELECT l.medicamento_id,
                           SUM(l.cantidad_disponible) AS cantidad
                    FROM medicamento_lote l
                    JOIN pendientes p ON p.medicamento_id = l.medicamento_id
                    WHERE l.cantidad_disponible > 0
                      AND l.fecha_ingreso <= CURRENT_DATE
                      AND l.fecha_vence >= CURRENT_DATE
                    GROUP BY l.medicamento_id
                )
                SELECT m.nombre
                FROM pendientes p
                JOIN medicamento m ON m.id = p.medicamento_id
                LEFT JOIN existencias x ON x.medicamento_id = p.medicamento_id
                WHERE p.cantidad > COALESCE(x.cantidad, 0)
                ORDER BY m.nombre
                LIMIT 1
                """, String.class, formulaId, formulaId);

        if (faltantes.isEmpty()) {
            throw error(HttpStatus.CONFLICT,
                    "No se encontró un faltante para las cantidades prescritas.");
        }

        jdbc.update("""
                UPDATE despacho SET estado = ? WHERE id = ?
                """, nuevoEstado, despacho.id());

        jdbc.update("""
                INSERT INTO despacho_historial
                    (despacho_id, usuario_id, estado_anterior, estado_nuevo, motivo)
                VALUES (?, ?, ?, ?, ?)
                """, despacho.id(), usuarioId, estadoAnterior, nuevoEstado,
                "Stock vigente insuficiente para " + faltantes.get(0));
    }
    public record CancelacionRq(String motivo) {
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void cancelarDespacho(String autorizacion, Long formulaId, CancelacionRq datos) {
        long usuarioId = obtenerUsuarioDespacho(autorizacion);

        if (formulaId == null || formulaId <= 0) {
            throw error(HttpStatus.BAD_REQUEST, "Indica un número de fórmula válido.");
        }

        String motivo = datos == null || datos.motivo() == null
                ? "" : datos.motivo().strip();

        if (motivo.isEmpty() || motivo.length() > 100) {
            throw error(HttpStatus.BAD_REQUEST,
                    "Escribe un motivo de cancelación de máximo 100 caracteres.");
        }

        List<Long> formulas = jdbc.queryForList("""
                SELECT id
                FROM formula_medica
                WHERE id = ?
                FOR UPDATE
                """, Long.class, formulaId);

        if (formulas.isEmpty()) {
            throw error(HttpStatus.NOT_FOUND, "La fórmula no existe.");
        }

        List<Despacho> despachos = jdbc.query("""
                SELECT id, estado
                FROM despacho
                WHERE formula_id = ?
                FOR UPDATE
                """, (rs, fila) -> new Despacho(
                rs.getLong("id"), rs.getString("estado")
        ), formulaId);

        if (despachos.isEmpty()) {
            throw error(HttpStatus.CONFLICT,
                    "Primero inicia la preparación del despacho.");
        }

        Despacho despacho = despachos.get(0);

        if ("CANCELADO".equals(despacho.estado())) {
            return;
        }

        Integer tieneEntregas = jdbc.queryForObject("""
                SELECT EXISTS (
                    SELECT 1 FROM despacho_entrega WHERE despacho_id = ?
                )
                """, Integer.class, despacho.id());

        if (Integer.valueOf(1).equals(tieneEntregas)) {
            throw error(HttpStatus.CONFLICT,
                    "Este despacho ya tiene entregas registradas.");
        }

        if (!"EN_PREPARACION".equals(despacho.estado())
                && !"PENDIENTE_DISPONIBILIDAD".equals(despacho.estado())) {
            throw error(HttpStatus.CONFLICT,
                    "El estado actual no permite cancelar este despacho.");
        }

        jdbc.update("""
                UPDATE despacho SET estado = 'CANCELADO' WHERE id = ?
                """, despacho.id());

        jdbc.update("""
                INSERT INTO despacho_historial
                    (despacho_id, usuario_id, estado_anterior, estado_nuevo, motivo)
                VALUES (?, ?, ?, 'CANCELADO', ?)
                """, despacho.id(), usuarioId, despacho.estado(), motivo);
    }
    public record CambioEstado(
            long id,
            String fecha,
            String estadoAnterior,
            String estadoNuevo,
            String responsable,
            String motivo
    ) {
    }

    @Transactional(readOnly = true)
    public List<CambioEstado> listarHistorial(String autorizacion, Long formulaId) {
        obtenerUsuarioDespacho(autorizacion);

        if (formulaId == null || formulaId <= 0) {
            throw error(HttpStatus.BAD_REQUEST, "Indica un número de fórmula válido.");
        }

        Integer existe = jdbc.queryForObject("""
                SELECT EXISTS (
                    SELECT 1 FROM formula_medica WHERE id = ?
                )
                """, Integer.class, formulaId);

        if (!Integer.valueOf(1).equals(existe)) {
            throw error(HttpStatus.NOT_FOUND, "La fórmula seleccionada no existe.");
        }

        return jdbc.query("""
                SELECT h.id,
                       DATE_FORMAT(h.fecha, '%d/%m/%Y %H:%i:%s') AS fecha,
                       h.estado_anterior,
                       h.estado_nuevo,
                       u.username AS responsable,
                       h.motivo
                FROM despacho_historial h
                JOIN despacho d ON d.id = h.despacho_id
                JOIN usuario u ON u.id = h.usuario_id
                WHERE d.formula_id = ?
                ORDER BY h.fecha DESC, h.id DESC
                """, (rs, fila) -> new CambioEstado(
                rs.getLong("id"),
                rs.getString("fecha"),
                rs.getString("estado_anterior"),
                rs.getString("estado_nuevo"),
                rs.getString("responsable"),
                rs.getString("motivo")
        ), formulaId);
    }
    public record SalidaMedicamento(
            long detalleId,
            long entregaId,
            long formulaId,
            String medicamento,
            String lote,
            long unidadesEntregadas,
            String fechaEntrega,
            String responsable,
            String mascota,
            String propietario,
            String medico,
            String estadoDespacho
    ) {}

    @Transactional(readOnly = true)
    public List<SalidaMedicamento> buscarSalidasPorMedicamento(
            String autorizacion, String nombre
    ) {
        obtenerUsuarioDespacho(autorizacion);

        String termino = nombre == null ? "" : nombre.strip();
        if (termino.isEmpty()) {
            throw error(HttpStatus.BAD_REQUEST, "Escribe el nombre de un medicamento.");
        }

        return jdbc.query("""
                SELECT dd.id AS detalle_id,
                       e.id AS entrega_id,
                       dd.formula_id,
                       m.nombre AS medicamento,
                       l.numero_lote,
                       dd.cantidad_entregada,
                       DATE_FORMAT(e.fecha_entrega, '%d/%m/%Y %H:%i:%s') AS fecha,
                       u.username AS responsable,
                       ma.nombre_mascota AS mascota,
                       CONCAT_WS(' ', cl.nombres, cl.apellidos) AS propietario,
                       CONCAT_WS(' ', me.nombres, me.apellidos) AS medico,
                       d.estado AS estado_despacho
                FROM despacho_detalle dd
                JOIN despacho_entrega e ON e.id = dd.entrega_id
                JOIN despacho d ON d.id = e.despacho_id
                JOIN medicamento_lote l ON l.id = dd.lote_id
                JOIN medicamento m ON m.id = l.medicamento_id
                JOIN usuario u ON u.id = e.usuario_id
                JOIN formula_medica f ON f.id = dd.formula_id
                JOIN cita c ON c.id = f.cita_id
                JOIN mascota ma ON ma.mascota_id = c.mascota_id
                JOIN cliente cl ON cl.cliente_id = c.cliente_id
                JOIN medico me ON me.id = c.medico_id
                WHERE LOCATE(LOWER(?), LOWER(m.nombre)) > 0
                ORDER BY e.fecha_entrega DESC, e.id DESC, dd.id DESC
                """, (rs, fila) -> new SalidaMedicamento(
                rs.getLong("detalle_id"),
                rs.getLong("entrega_id"),
                rs.getLong("formula_id"),
                rs.getString("medicamento"),
                rs.getString("numero_lote"),
                rs.getLong("cantidad_entregada"),
                rs.getString("fecha"),
                rs.getString("responsable"),
                rs.getString("mascota"),
                rs.getString("propietario"),
                rs.getString("medico"),
                rs.getString("estado_despacho")
        ), termino);
    }

    private long obtenerUsuarioDespacho(String autorizacion) {
        if (autorizacion == null || !autorizacion.startsWith("Bearer ")) {
            throw error(HttpStatus.UNAUTHORIZED, "Inicia sesión para registrar la entrega.");
        }

        String token = autorizacion.substring(7).trim();
        if (token.isEmpty()) {
            throw error(HttpStatus.UNAUTHORIZED, "La sesión no es válida.");
        }

        record Cuenta(long id, String perfil) {}

        List<Cuenta> cuentas = jdbc.query("""
                SELECT u.id, p.nombre
                FROM `session` s
                JOIN usuario u ON u.id = s.user_id
                JOIN perfil p ON p.perfil_id = u.rol
                WHERE BINARY s.token = BINARY ?
                  AND s.fecha_expiracion > CURRENT_TIMESTAMP
                  AND u.activo = 1
                  AND p.activo = 1
                ORDER BY s.session_id DESC
                LIMIT 1
                """, (rs, fila) -> new Cuenta(
                rs.getLong("id"), rs.getString("nombre")
        ), token);

        if (cuentas.isEmpty()) {
            throw error(HttpStatus.UNAUTHORIZED, "La sesión venció. Vuelve a ingresar.");
        }

        Cuenta cuenta = cuentas.get(0);
        if (!"DESPACHO_MEDICO".equalsIgnoreCase(cuenta.perfil().trim())) {
            throw error(HttpStatus.FORBIDDEN,
                    "Solo el perfil de despacho puede confirmar entregas.");
        }

        return cuenta.id();
    }

    private void validarSolicitud(EntregaRq datos) {
        if (datos == null
                || datos.formulaId() == null || datos.formulaId() <= 0
                || datos.numeroLinea() == null || datos.numeroLinea() <= 0
                || datos.loteId() == null || datos.loteId() <= 0
                || datos.cantidad() == null || datos.cantidad() <= 0
                || datos.claveOperacion() == null) {
            throw error(HttpStatus.BAD_REQUEST,
                    "Indica la fórmula, la línea, el lote, una cantidad positiva y la clave de operación.");
        }
    }

    private long ultimoId() {
        return jdbc.queryForObject("SELECT LAST_INSERT_ID()", Long.class);
    }

    private ResponseStatusException error(HttpStatus estado, String mensaje) {
        return new ResponseStatusException(estado, mensaje);
    }
}