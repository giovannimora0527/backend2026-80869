package com.uniminuto.clinica.models;

import java.time.LocalDateTime;

/**
 * Datos recibidos para crear o editar una fórmula médica. En la edición solo se
 * toman en cuenta dosis, frecuencia, duración e indicaciones.
 */
public class FormulaMedicaRq {

    /** Cita (consulta) en la que se emite la fórmula; obligatoria al crear. */
    private Long citaId;

    /** Mascota; opcional, si no se envía se toma de la cita. */
    private Integer mascotaId;

    /** Veterinario; opcional, si no se envía se toma de la cita. */
    private Long medicoId;

    /** Historia clínica; opcional, si no se envía se busca la de la cita. */
    private Long historiaId;

    /** Medicamento recetado; obligatorio al crear. */
    private Long medicamentoId;

    /** Dosis, por ejemplo "500 mg"; debe contener una cantidad mayor que cero. */
    private String dosis;

    /** Frecuencia, por ejemplo "cada 8 horas". */
    private String frecuencia;

    /** Duración del tratamiento en días (entre 1 y 365). */
    private Integer duracionDias;

    /** Indicaciones para el cliente. */
    private String indicaciones;

    /** Fecha y hora de emisión; opcional, por defecto la actual. */
    private LocalDateTime fechaEmision;

    public Long getCitaId() { return citaId; }
    public void setCitaId(Long citaId) { this.citaId = citaId; }
    public Integer getMascotaId() { return mascotaId; }
    public void setMascotaId(Integer mascotaId) { this.mascotaId = mascotaId; }
    public Long getMedicoId() { return medicoId; }
    public void setMedicoId(Long medicoId) { this.medicoId = medicoId; }
    public Long getHistoriaId() { return historiaId; }
    public void setHistoriaId(Long historiaId) { this.historiaId = historiaId; }
    public Long getMedicamentoId() { return medicamentoId; }
    public void setMedicamentoId(Long medicamentoId) { this.medicamentoId = medicamentoId; }
    public String getDosis() { return dosis; }
    public void setDosis(String dosis) { this.dosis = dosis; }
    public String getFrecuencia() { return frecuencia; }
    public void setFrecuencia(String frecuencia) { this.frecuencia = frecuencia; }
    public Integer getDuracionDias() { return duracionDias; }
    public void setDuracionDias(Integer duracionDias) { this.duracionDias = duracionDias; }
    public String getIndicaciones() { return indicaciones; }
    public void setIndicaciones(String indicaciones) { this.indicaciones = indicaciones; }
    public LocalDateTime getFechaEmision() { return fechaEmision; }
    public void setFechaEmision(LocalDateTime fechaEmision) { this.fechaEmision = fechaEmision; }
}
