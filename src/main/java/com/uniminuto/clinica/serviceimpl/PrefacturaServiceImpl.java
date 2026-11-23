package com.uniminuto.clinica.serviceimpl;

import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Service
public class PrefacturaServiceImpl {

    private final JdbcTemplate jdbc;

    public PrefacturaServiceImpl(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private record Cuenta(long id, String perfil) {}

    private record Cita(
            long id,
            LocalDate fecha,
            String estado,
            String propietario,
            String mascota,
            String medico,
            Long usuarioMedicoId,
            long especialidadId,
            String especialidad
    ) {}

    public record TarifaAplicada(
            long servicioId,
            String servicio,
            LocalDate vigenteDesde,
            BigDecimal valorBase,
            BigDecimal porcentajeIva
    ) {}

    public record MedicamentoFormulado(
            long formulaId,
            int numeroLinea,
            String medicamento,
            Long cantidad,
            BigDecimal precioUnitario,
            BigDecimal subtotal,
            String estadoDespacho
    ) {}

    public record Prefactura(
            long citaId,
            LocalDate fechaAtencion,
            String propietario,
            String mascota,
            String medico,
            String especialidad,
            TarifaAplicada tarifa,
            List<MedicamentoFormulado> medicamentos,
            BigDecimal subtotalMedicamentos,
            BigDecimal ivaServicio,
            BigDecimal totalEstimado
    ) {}

    @Transactional(readOnly = true)
    public Prefactura consultar(String autorizacion, long citaId, Long servicioId) {
        Cuenta cuenta = obtenerCuenta(autorizacion);

        if (citaId <= 0 || (servicioId != null && servicioId <= 0)) {
            throw error(HttpStatus.BAD_REQUEST, "Selecciona una cita y un servicio válidos.");
        }

        List<Cita> citas = jdbc.query("""
                SELECT c.id, DATE(c.fecha_hora) AS fecha, c.estado,
                       CONCAT_WS(' ', cl.nombres, cl.apellidos) AS propietario,
                       ma.nombre_mascota AS mascota,
                       CONCAT_WS(' ', me.nombres, me.apellidos) AS medico,
                       me.usuario_id, e.id AS especialidad_id, e.nombre AS especialidad
                FROM cita c
                JOIN cliente cl ON cl.cliente_id = c.cliente_id
                JOIN mascota ma ON ma.mascota_id = c.mascota_id
                JOIN medico me ON me.id = c.medico_id
                JOIN especializacion e ON e.id = me.especializacion_id
                WHERE c.id = ?
                """, (rs, fila) -> new Cita(
                rs.getLong("id"),
                rs.getObject("fecha", LocalDate.class),
                rs.getString("estado"),
                rs.getString("propietario"),
                rs.getString("mascota"),
                rs.getString("medico"),
                rs.getObject("usuario_id", Long.class),
                rs.getLong("especialidad_id"),
                rs.getString("especialidad")
        ), citaId);

        if (citas.isEmpty()) {
            throw error(HttpStatus.NOT_FOUND, "La cita no existe.");
        }

        Cita cita = citas.get(0);

        if ("Medico".equalsIgnoreCase(cuenta.perfil())
                && !Long.valueOf(cuenta.id()).equals(cita.usuarioMedicoId())) {
            throw error(HttpStatus.FORBIDDEN,
                    "Solo puedes consultar la prefactura de tus propias citas.");
        }

        if (!"ATENDIDA".equalsIgnoreCase(cita.estado())) {
            throw error(HttpStatus.CONFLICT,
                    "La cita debe estar atendida para generar la prefactura.");
        }

        TarifaAplicada tarifa = obtenerTarifa(cita, servicioId);

        List<MedicamentoFormulado> medicamentos = jdbc.query("""
                SELECT formula_id, numero_linea, medicamento, cantidad_prescrita,
                       precio_unitario, subtotal_estimado, estado_despacho
                FROM v_prefactura_medicamento
                WHERE cita_id = ?
                ORDER BY formula_id, numero_linea
                """, (rs, fila) -> new MedicamentoFormulado(
                rs.getLong("formula_id"),
                rs.getInt("numero_linea"),
                rs.getString("medicamento"),
                rs.getObject("cantidad_prescrita", Long.class),
                rs.getBigDecimal("precio_unitario"),
                rs.getBigDecimal("subtotal_estimado"),
                rs.getString("estado_despacho")
        ), citaId);

        BigDecimal subtotalMedicamentos = BigDecimal.ZERO;

        for (MedicamentoFormulado linea : medicamentos) {
            if (linea.subtotal() == null) {
                throw error(HttpStatus.CONFLICT,
                        "Falta cantidad o precio para " + linea.medicamento()
                                + " en la fórmula " + linea.formulaId() + ".");
            }
            subtotalMedicamentos = subtotalMedicamentos.add(linea.subtotal());
        }

        BigDecimal ivaServicio = tarifa.valorBase()
                .multiply(tarifa.porcentajeIva())
                .movePointLeft(2)
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal total = tarifa.valorBase()
                .add(ivaServicio)
                .add(subtotalMedicamentos);

        return new Prefactura(
                cita.id(), cita.fecha(), cita.propietario(), cita.mascota(),
                cita.medico(), cita.especialidad(), tarifa, medicamentos,
                subtotalMedicamentos, ivaServicio, total
        );
    }

    private TarifaAplicada obtenerTarifa(Cita cita, Long servicioId) {
        // Se toma una sola vigencia por servicio, según el día de la atención.
        String sql = """
                SELECT t.id, t.nombre_servicio, v.vigente_desde,
                       v.valor_base, v.porcentaje_iva
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

        Object[] parametros;
        if (servicioId == null) {
            parametros = new Object[] { cita.fecha(), cita.especialidadId() };
        } else {
            sql += " AND t.id = ?";
            parametros = new Object[] { cita.fecha(), cita.especialidadId(), servicioId };
        }

        List<TarifaAplicada> tarifas = jdbc.query(sql, (rs, fila) -> new TarifaAplicada(
                rs.getLong("id"),
                rs.getString("nombre_servicio"),
                rs.getObject("vigente_desde", LocalDate.class),
                rs.getBigDecimal("valor_base"),
                rs.getBigDecimal("porcentaje_iva")
        ), parametros);

        if (tarifas.isEmpty()) {
            throw error(HttpStatus.CONFLICT,
                    "No hay tarifa aplicable para esa especialidad y fecha de atención.");
        }

        if (tarifas.size() > 1) {
            throw error(HttpStatus.CONFLICT,
                    "Hay varios servicios con tarifa. Selecciona el servicio prestado.");
        }

        return tarifas.get(0);
    }

    private Cuenta obtenerCuenta(String autorizacion) {
        if (autorizacion == null || !autorizacion.startsWith("Bearer ")) {
            throw error(HttpStatus.UNAUTHORIZED, "Inicia sesión.");
        }

        String token = autorizacion.substring(7).trim();
        if (token.isEmpty()) {
            throw error(HttpStatus.UNAUTHORIZED, "La sesión no es válida.");
        }

        List<Cuenta> cuentas = jdbc.query("""
                SELECT u.id, p.nombre AS perfil
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
                rs.getLong("id"), rs.getString("perfil").trim()
        ), token);

        if (cuentas.isEmpty()) {
            throw error(HttpStatus.UNAUTHORIZED, "La sesión venció. Vuelve a ingresar.");
        }

        Cuenta cuenta = cuentas.get(0);
        if (!"Administrador".equalsIgnoreCase(cuenta.perfil())
                && !"Medico".equalsIgnoreCase(cuenta.perfil())) {
            throw error(HttpStatus.FORBIDDEN,
                    "Tu perfil no tiene acceso a la prefactura.");
        }

        return cuenta;
    }

    private ResponseStatusException error(HttpStatus estado, String mensaje) {
        return new ResponseStatusException(estado, mensaje);
    }
}
