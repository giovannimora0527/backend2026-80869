package com.uniminuto.clinica.models;

import java.time.LocalDateTime;

/**
 * Datos recibidos para crear una historia clínica a partir de una cita atendida.
 */
public class HistoriaMedicaRq {

    /** Cliente propietario de la mascota. */
    private Long clienteId;

    /** Mascota de la historia; debe pertenecer al cliente. */
    private Integer mascotaId;

    /** Cita atendida que origina la historia; debe ser de la misma mascota. */
    private Long citaId;

    /** Veterinario responsable; opcional, por defecto el de la cita. */
    private Long medicoId;

    /** Fecha y hora de la consulta; opcional, por defecto la actual. */
    private LocalDateTime fechaHora;

    /** Motivo de la consulta. */
    private String motivo;

    /** Diagnóstico de la consulta. */
    private String diagnostico;

    /** Observaciones adicionales. */
    private String observaciones;

    public Long getClienteId() { return clienteId; }
    public void setClienteId(Long clienteId) { this.clienteId = clienteId; }
    public Integer getMascotaId() { return mascotaId; }
    public void setMascotaId(Integer mascotaId) { this.mascotaId = mascotaId; }
    public Long getCitaId() { return citaId; }
    public void setCitaId(Long citaId) { this.citaId = citaId; }
    public Long getMedicoId() { return medicoId; }
    public void setMedicoId(Long medicoId) { this.medicoId = medicoId; }
    public LocalDateTime getFechaHora() { return fechaHora; }
    public void setFechaHora(LocalDateTime fechaHora) { this.fechaHora = fechaHora; }
    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
    public String getDiagnostico() { return diagnostico; }
    public void setDiagnostico(String diagnostico) { this.diagnostico = diagnostico; }
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
}
