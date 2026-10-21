package com.uniminuto.clinica.models;

import java.time.LocalDateTime;

/**
 * Datos recibidos para agregar una anotación a una historia clínica existente.
 */
public class AnotacionHistoriaRq {

    /** Historia clínica a la que se agrega la anotación. */
    private Long historiaId;

    /** Veterinario que escribe la anotación. */
    private Long medicoId;

    /** Fecha y hora de la anotación; opcional, por defecto la actual. */
    private LocalDateTime fecha;

    /** Descripción de lo realizado u observado durante el seguimiento. */
    private String descripcion;

    /** Observaciones adicionales del veterinario. */
    private String observacion;

    /** Diagnóstico o evolución del paciente. */
    private String diagnostico;

    public Long getHistoriaId() { return historiaId; }
    public void setHistoriaId(Long historiaId) { this.historiaId = historiaId; }
    public Long getMedicoId() { return medicoId; }
    public void setMedicoId(Long medicoId) { this.medicoId = medicoId; }
    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public String getObservacion() { return observacion; }
    public void setObservacion(String observacion) { this.observacion = observacion; }
    public String getDiagnostico() { return diagnostico; }
    public void setDiagnostico(String diagnostico) { this.diagnostico = diagnostico; }
}
