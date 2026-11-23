package com.uniminuto.clinica.serviceimpl;

import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@Service
public class ReporteGestionMedicaServiceImpl {

    private final JdbcTemplate jdbc;
    private final TarifaServiceImpl tarifas;

    public ReporteGestionMedicaServiceImpl(JdbcTemplate jdbc, TarifaServiceImpl tarifas) {
        this.jdbc = jdbc;
        this.tarifas = tarifas;
    }

    public record Resumen(
            long totalCitas,
            long citasAtendidas,
            long mascotasAtendidas
    ) {}

    public record FilaMedica(
            long medicoId,
            String medico,
            String especialidad,
            String estado,
            long cantidad
    ) {}

    public record Reporte(
            LocalDate desde,
            LocalDate hasta,
            Resumen resumen,
            List<FilaMedica> detalle
    ) {}

    @Transactional(readOnly = true)
    public Reporte consultar(String autorizacion, LocalDate desde, LocalDate hasta) {
        tarifas.obtenerAdministrador(autorizacion);

        if (desde == null || hasta == null || hasta.isBefore(desde)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Selecciona un rango de fechas válido."
            );
        }

        Resumen resumen = jdbc.queryForObject("""
                SELECT COUNT(*) AS total_citas,
                       COUNT(CASE WHEN estado = 'ATENDIDA' THEN 1 END) AS atendidas,
                       COUNT(DISTINCT CASE
                           WHEN estado = 'ATENDIDA' THEN mascota_id
                       END) AS mascotas_atendidas
                FROM cita
                WHERE fecha_hora >= ?
                  AND fecha_hora < DATE_ADD(?, INTERVAL 1 DAY)
                """, (rs, fila) -> new Resumen(
                rs.getLong("total_citas"),
                rs.getLong("atendidas"),
                rs.getLong("mascotas_atendidas")
        ), desde, hasta);

        List<FilaMedica> detalle = jdbc.query("""
                SELECT m.id AS medico_id,
                       CONCAT_WS(' ', m.nombres, m.apellidos) AS medico,
                       COALESCE(e.nombre, 'Sin especialidad') AS especialidad,
                       c.estado,
                       COUNT(*) AS cantidad
                FROM cita c
                JOIN medico m ON m.id = c.medico_id
                LEFT JOIN especializacion e ON e.id = m.especializacion_id
                WHERE c.fecha_hora >= ?
                  AND c.fecha_hora < DATE_ADD(?, INTERVAL 1 DAY)
                GROUP BY m.id, m.nombres, m.apellidos, e.id, e.nombre, c.estado
                ORDER BY especialidad, medico, m.id, c.estado
                """, (rs, fila) -> new FilaMedica(
                rs.getLong("medico_id"),
                rs.getString("medico"),
                rs.getString("especialidad"),
                rs.getString("estado"),
                rs.getLong("cantidad")
        ), desde, hasta);

        return new Reporte(desde, hasta, resumen, detalle);
    }
}