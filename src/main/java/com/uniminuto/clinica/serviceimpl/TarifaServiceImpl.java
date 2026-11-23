package com.uniminuto.clinica.serviceimpl;

import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TarifaServiceImpl {

    private final JdbcTemplate jdbc;

    public TarifaServiceImpl(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public record ServicioTarifado(
            long id,
            String especialidad,
            String nombreServicio,
            boolean activo
    ) {}

    public record Vigencia(
            long id,
            LocalDate vigenteDesde,
            BigDecimal valorBase,
            BigDecimal porcentajeIva,
            String usuario,
            LocalDateTime fechaRegistro,
            String motivo
    ) {}

    public record NuevaVigencia(
            LocalDate vigenteDesde,
            BigDecimal valorBase,
            BigDecimal porcentajeIva,
            String motivo
    ) {}

    private record Cuenta(long id, String perfil) {}

    @Transactional(readOnly = true)
    public List<ServicioTarifado> listarServicios(String autorizacion) {
        obtenerAdministrador(autorizacion);

        return jdbc.query("""
                SELECT t.id, e.nombre AS especialidad,
                       t.nombre_servicio, t.activo
                FROM tarifa_servicio t
                JOIN especializacion e ON e.id = t.especializacion_id
                ORDER BY e.nombre, t.nombre_servicio, t.id
                """, (rs, fila) -> new ServicioTarifado(
                rs.getLong("id"),
                rs.getString("especialidad"),
                rs.getString("nombre_servicio"),
                rs.getBoolean("activo")
        ));
    }

    @Transactional(readOnly = true)
    public List<Vigencia> listarHistorial(String autorizacion, long servicioId) {
        obtenerAdministrador(autorizacion);

        List<Long> servicios = jdbc.queryForList("""
                SELECT id FROM tarifa_servicio WHERE id = ?
                """, Long.class, servicioId);

        if (servicios.isEmpty()) {
            throw error(HttpStatus.NOT_FOUND, "El servicio no existe.");
        }

        return jdbc.query("""
                SELECT v.id, v.vigente_desde, v.valor_base, v.porcentaje_iva,
                       u.username, v.fecha_registro, v.motivo
                FROM tarifa_vigencia v
                JOIN usuario u ON u.id = v.usuario_id
                WHERE v.tarifa_servicio_id = ?
                ORDER BY v.vigente_desde DESC, v.id DESC
                """, (rs, fila) -> new Vigencia(
                rs.getLong("id"),
                rs.getObject("vigente_desde", LocalDate.class),
                rs.getBigDecimal("valor_base"),
                rs.getBigDecimal("porcentaje_iva"),
                rs.getString("username"),
                rs.getObject("fecha_registro", LocalDateTime.class),
                rs.getString("motivo")
        ), servicioId);
    }

    @Transactional
    public void registrarVigencia(
            String autorizacion, long servicioId, NuevaVigencia datos
    ) {
        long usuarioId = obtenerAdministrador(autorizacion);

        if (datos == null || datos.vigenteDesde() == null) {
            throw error(HttpStatus.BAD_REQUEST, "Selecciona la fecha de vigencia.");
        }

        validarImporte(datos.valorBase(), new BigDecimal("9999999999.99"),
                "El valor base");
        validarImporte(datos.porcentajeIva(), new BigDecimal("100.00"),
                "El porcentaje de IVA");

        String motivo = datos.motivo() == null ? "" : datos.motivo().strip();
        if (motivo.isEmpty() || motivo.length() > 255) {
            throw error(HttpStatus.BAD_REQUEST,
                    "Escribe un motivo de entre 1 y 255 caracteres.");
        }

        // Dos administradores que modifiquen el mismo servicio se procesan en orden.
        List<Boolean> servicios = jdbc.query("""
                SELECT activo FROM tarifa_servicio WHERE id = ? FOR UPDATE
                """, (rs, fila) -> rs.getBoolean("activo"), servicioId);

        if (servicios.isEmpty()) {
            throw error(HttpStatus.NOT_FOUND, "El servicio no existe.");
        }

        if (!servicios.get(0)) {
            throw error(HttpStatus.CONFLICT, "El servicio está inactivo.");
        }

        List<Long> mismaFecha = jdbc.queryForList("""
                SELECT id FROM tarifa_vigencia
                WHERE tarifa_servicio_id = ? AND vigente_desde = ?
                FOR UPDATE
                """, Long.class, servicioId, datos.vigenteDesde());

        if (!mismaFecha.isEmpty()) {
            throw error(HttpStatus.CONFLICT,
                    "Este servicio ya tiene una tarifa registrada para esa fecha.");
        }

        jdbc.update("""
                INSERT INTO tarifa_vigencia
                    (tarifa_servicio_id, vigente_desde, valor_base,
                     porcentaje_iva, usuario_id, motivo)
                VALUES (?, ?, ?, ?, ?, ?)
                """, servicioId, datos.vigenteDesde(), datos.valorBase(),
                datos.porcentajeIva(), usuarioId, motivo);
    }

    private void validarImporte(BigDecimal valor, BigDecimal maximo, String campo) {
        if (valor == null || valor.signum() < 0 || valor.compareTo(maximo) > 0
                || valor.stripTrailingZeros().scale() > 2) {
            throw error(HttpStatus.BAD_REQUEST, campo + " debe estar entre 0 y "
                    + maximo.toPlainString() + ", con máximo dos decimales.");
        }
    }

    public long obtenerAdministrador(String autorizacion) {
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
                rs.getLong("id"), rs.getString("perfil")
        ), token);

        if (cuentas.isEmpty()) {
            throw error(HttpStatus.UNAUTHORIZED, "La sesión venció. Vuelve a ingresar.");
        }

        Cuenta cuenta = cuentas.get(0);
        if (!"Administrador".equalsIgnoreCase(cuenta.perfil().trim())) {
            throw error(HttpStatus.FORBIDDEN,
                    "Solo el perfil Administrador puede gestionar tarifas.");
        }

        return cuenta.id();
    }

    private ResponseStatusException error(HttpStatus estado, String mensaje) {
        return new ResponseStatusException(estado, mensaje);
    }
}