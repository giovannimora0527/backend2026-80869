package com.uniminuto.clinica.serviceimpl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import com.uniminuto.clinica.exception.BadRequestException;

@Service
public class ServiciosAtencionService {
    private final JdbcTemplate jdbc;

    public ServiciosAtencionService(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public record Servicio(long vigenciaId, String nombre, BigDecimal valor, BigDecimal porcentajeIva) {
        public BigDecimal calcularIva() {
            return valor.multiply(porcentajeIva).movePointLeft(2).setScale(2, RoundingMode.HALF_UP);
        }
    }

    public List<Servicio> disponibles(long citaId, long medicoId) {
        return jdbc.query("""
                SELECT v.id, t.nombre_servicio, v.valor_base, v.porcentaje_iva
                FROM cita c
                JOIN medico m ON m.id = c.medico_id
                JOIN tarifa_servicio t ON t.especializacion_id = m.especializacion_id
                JOIN tarifa_vigencia v ON v.tarifa_servicio_id = t.id
                  AND v.vigente_desde = (
                    SELECT MAX(v2.vigente_desde) FROM tarifa_vigencia v2
                    WHERE v2.tarifa_servicio_id = t.id AND v2.vigente_desde <= DATE(c.fecha_hora)
                  )
                WHERE c.id = ? AND c.medico_id = ? AND t.activo = 1
                  AND t.nombre_servicio NOT LIKE 'Consulta %'
                ORDER BY t.nombre_servicio
                """, (rs, fila) -> new Servicio(rs.getLong(1), rs.getString(2),
                rs.getBigDecimal(3), rs.getBigDecimal(4)), citaId, medicoId);
    }

    public void guardar(long citaId, long medicoId, List<Long> vigencias) {
        if (vigencias == null || vigencias.isEmpty()) return;
        List<Long> permitidas = disponibles(citaId, medicoId).stream().map(Servicio::vigenciaId).toList();
        if (!permitidas.containsAll(vigencias)) {
            throw new BadRequestException("Un servicio seleccionado ya no tiene tarifa aplicable.");
        }
        for (Long id : vigencias.stream().distinct().toList()) {
            jdbc.update("INSERT INTO cita_servicio (cita_id, tarifa_vigencia_id) VALUES (?, ?)", citaId, id);
        }
    }

    public List<Servicio> guardados(long citaId) {
        return jdbc.query("""
                SELECT v.id, t.nombre_servicio, v.valor_base, v.porcentaje_iva
                FROM cita_servicio cs
                JOIN tarifa_vigencia v ON v.id = cs.tarifa_vigencia_id
                JOIN tarifa_servicio t ON t.id = v.tarifa_servicio_id
                WHERE cs.cita_id = ?
                ORDER BY t.nombre_servicio
                """, (rs, fila) -> new Servicio(rs.getLong(1), rs.getString(2),
                rs.getBigDecimal(3), rs.getBigDecimal(4)), citaId);
    }
}
