package com.uniminuto.clinica.models;

import com.uniminuto.clinica.entity.HistoriaMedica;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Fila resumida de una historia clínica para el DataTable del listado.
 * El detalle con anotaciones y fórmulas se consulta aparte (GET /historia-clinica/{id}).
 */
@Data
public class HistoriaResumenRs {

    private Long id;
    private LocalDateTime fechaConsulta;
    private Integer mascotaId;
    private String mascotaNombre;
    private Long clienteId;
    private String clienteNombre;
    private Long medicoId;
    private String medicoNombre;
    private Long citaId;
    private String motivo;
    /** Estado de la cita asociada (ATENDIDA, etc.). */
    private String estado;
    private long totalAnotaciones;

    /**
     * Construye la fila resumida a partir de la entidad.
     *
     * @param h               historia a resumir.
     * @param totalAnotaciones cantidad de anotaciones de la historia.
     * @return la fila con los datos que muestra el listado.
     */
    public static HistoriaResumenRs de(HistoriaMedica h, long totalAnotaciones) {
        HistoriaResumenRs rs = new HistoriaResumenRs();
        rs.setId(h.getId());
        rs.setFechaConsulta(h.getFechaConsulta() != null ? h.getFechaConsulta() : h.getFechaCreacion());
        if (h.getMascota() != null) {
            rs.setMascotaId(h.getMascota().getMascotaId());
            rs.setMascotaNombre(h.getMascota().getNombreMascota());
            if (h.getMascota().getCliente() != null) {
                rs.setClienteId(h.getMascota().getCliente().getId());
                rs.setClienteNombre(h.getMascota().getCliente().getNombres()
                        + " " + h.getMascota().getCliente().getApellidos());
            }
        }
        if (h.getMedico() != null) {
            rs.setMedicoId(h.getMedico().getId());
            rs.setMedicoNombre(h.getMedico().getNombres() + " " + h.getMedico().getApellidos());
        }
        if (h.getCita() != null) {
            rs.setCitaId(h.getCita().getId());
            rs.setEstado(h.getCita().getEstado());
        }
        rs.setMotivo(h.getMotivo());
        rs.setTotalAnotaciones(totalAnotaciones);
        return rs;
    }
}
