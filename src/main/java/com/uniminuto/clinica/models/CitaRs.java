package com.uniminuto.clinica.models;

import com.uniminuto.clinica.entity.Cita;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Fila resumida de una cita para el DataTable del listado.
 * El detalle completo se consulta aparte (GET /cita/{id}).
 */
@Data
public class CitaRs {

    private Long id;
    private LocalDateTime fechaHora;
    private Long clienteId;
    private String clienteNombre;
    private Integer mascotaId;
    private String mascotaNombre;
    private Long medicoId;
    private String medicoNombre;
    private String motivo;
    private String estado;

    /**
     * Construye la fila resumida a partir de la entidad.
     *
     * @param cita cita a resumir.
     * @return la fila con los datos que muestra el listado.
     */
    public static CitaRs de(Cita cita) {
        CitaRs rs = new CitaRs();
        rs.setId(cita.getId());
        rs.setFechaHora(cita.getFechaHora());
        if (cita.getCliente() != null) {
            rs.setClienteId(cita.getCliente().getId());
            rs.setClienteNombre(cita.getCliente().getNombres() + " " + cita.getCliente().getApellidos());
        }
        if (cita.getMascota() != null) {
            rs.setMascotaId(cita.getMascota().getMascotaId());
            rs.setMascotaNombre(cita.getMascota().getNombreMascota());
        }
        if (cita.getMedico() != null) {
            rs.setMedicoId(cita.getMedico().getId());
            rs.setMedicoNombre(cita.getMedico().getNombres() + " " + cita.getMedico().getApellidos());
        }
        rs.setMotivo(cita.getMotivo());
        rs.setEstado(cita.getEstado());
        return rs;
    }
}
