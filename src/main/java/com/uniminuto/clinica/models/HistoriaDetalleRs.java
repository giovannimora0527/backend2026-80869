package com.uniminuto.clinica.models;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/** Detalle completo de una historia clínica (incluye los campos que el listado omite). */
@Data
@EqualsAndHashCode(callSuper = true)
public class HistoriaDetalleRs extends HistoriaResumenRs {
    /** Diagnóstico emitido. */
    private String diagnostico;
    /** Observaciones o recomendaciones. */
    private String observaciones;
    /** Fecha de creación del registro en el sistema. */
    private LocalDateTime fechaCreacion;
    /** Cantidad de anotaciones registradas. */
    private long totalAnotaciones;
}
