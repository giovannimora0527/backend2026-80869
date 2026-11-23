package com.uniminuto.clinica.apicontroller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/formula-medica")
public class CitaFormulaApiController {

    private final JdbcTemplate jdbc;

    public CitaFormulaApiController(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public record CitaParaFormula(
            long id,
            String mascota,
            String propietario,
            String fechaHora
    ) {}

    private record Cuenta(long id, String perfil) {}

    @GetMapping(value = "/citas-atendidas", produces = "application/json")
    public List<CitaParaFormula> listarCitasAtendidas(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false)
            String autorizacion
    ) {
        if (autorizacion == null || !autorizacion.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Inicia sesión.");
        }

        String token = autorizacion.substring(7).trim();

        if (token.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "La sesión no es válida.");
        }

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
                rs.getLong("id"),
                rs.getString("nombre").trim()
        ), token);

        if (cuentas.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "La sesión venció.");
        }

        Cuenta cuenta = cuentas.get(0);
        boolean administrador = "Administrador".equalsIgnoreCase(cuenta.perfil());

        if (!administrador && !"Medico".equalsIgnoreCase(cuenta.perfil())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Tu perfil no tiene acceso a la selección de citas.");
        }

        return jdbc.query("""
                SELECT c.id,
                       ma.nombre_mascota AS mascota,
                       CONCAT_WS(' ', cl.nombres, cl.apellidos) AS propietario,
                       DATE_FORMAT(c.fecha_hora, '%d/%m/%Y %H:%i') AS fecha_hora
                FROM cita c
                JOIN mascota ma ON ma.mascota_id = c.mascota_id
                JOIN cliente cl ON cl.cliente_id = c.cliente_id
                JOIN medico me ON me.id = c.medico_id
                WHERE c.estado = 'ATENDIDA'
                  AND (? = 1 OR me.usuario_id = ?)
                ORDER BY c.fecha_hora DESC, c.id DESC
                """, (rs, fila) -> new CitaParaFormula(
                rs.getLong("id"),
                rs.getString("mascota"),
                rs.getString("propietario"),
                rs.getString("fecha_hora")
        ), administrador ? 1 : 0, cuenta.id());
    }
}
