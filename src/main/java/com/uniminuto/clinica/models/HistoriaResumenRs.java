package com.uniminuto.clinica.models;

import com.uniminuto.clinica.entity.HistoriaMedica;
import com.uniminuto.clinica.util.TextoUtil;

import java.time.LocalDateTime;

/**
 * Vista resumida de una historia clínica para el listado (DataTable). El detalle
 * completo se obtiene con {@code GET /historia-medica/consultar/{id}}.
 */
public class HistoriaResumenRs {

    /** Identificador de la historia. */
    private Long id;

    /** Fecha y hora de la consulta. */
    private LocalDateTime fechaHora;

    /** Identificador de la mascota. */
    private Integer mascotaId;

    /** Nombre de la mascota. */
    private String mascotaNombre;

    /** Identificador del cliente. */
    private Long clienteId;

    /** Nombre completo del cliente. */
    private String clienteNombre;

    /** Identificador del veterinario. */
    private Long medicoId;

    /** Nombre completo del veterinario. */
    private String medicoNombre;

    /** Identificador de la cita que originó la historia. */
    private Long citaId;

    /** Motivo de la consulta. */
    private String motivo;

    /** Estado de la historia (Abierta o Cerrada). */
    private String estado;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public LocalDateTime getFechaHora() { return fechaHora; }
    public void setFechaHora(LocalDateTime fechaHora) { this.fechaHora = fechaHora; }
    public Integer getMascotaId() { return mascotaId; }
    public void setMascotaId(Integer mascotaId) { this.mascotaId = mascotaId; }
    public String getMascotaNombre() { return mascotaNombre; }
    public void setMascotaNombre(String mascotaNombre) { this.mascotaNombre = mascotaNombre; }
    public Long getClienteId() { return clienteId; }
    public void setClienteId(Long clienteId) { this.clienteId = clienteId; }
    public String getClienteNombre() { return clienteNombre; }
    public void setClienteNombre(String clienteNombre) { this.clienteNombre = clienteNombre; }
    public Long getMedicoId() { return medicoId; }
    public void setMedicoId(Long medicoId) { this.medicoId = medicoId; }
    public String getMedicoNombre() { return medicoNombre; }
    public void setMedicoNombre(String medicoNombre) { this.medicoNombre = medicoNombre; }
    public Long getCitaId() { return citaId; }
    public void setCitaId(Long citaId) { this.citaId = citaId; }
    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    /**
     * Construye el resumen a partir de la entidad.
     *
     * @param historia historia a resumir
     * @return resumen de la historia
     */
    public static HistoriaResumenRs desde(HistoriaMedica historia) {
        HistoriaResumenRs rs = new HistoriaResumenRs();
        rs.setId(historia.getId());
        rs.setFechaHora(historia.getFechaCreacion());
        rs.setMascotaId(historia.getMascota().getMascotaId());
        rs.setMascotaNombre(historia.getMascota().getNombreMascota());
        rs.setClienteId(historia.getCliente().getId());
        rs.setClienteNombre(TextoUtil.nombreCompleto(historia.getCliente().getNombres(), historia.getCliente().getApellidos()));
        rs.setMedicoId(historia.getMedico().getId());
        rs.setMedicoNombre(TextoUtil.nombreCompleto(historia.getMedico().getNombres(), historia.getMedico().getApellidos()));
        rs.setCitaId(historia.getCita().getId());
        rs.setMotivo(historia.getMotivo());
        rs.setEstado(historia.getEstado());
        return rs;
    }
}
