package com.uniminuto.clinica.models;

import lombok.Data;

import java.time.LocalDateTime;

/** Representación de una anotación de historia clínica. */
@Data
public class AnotacionRs {
    /** Identificador de la anotación. */
    private Long id;
    /** Historia clínica a la que pertenece. */
    private Long historiaId;
    /** Identificador del veterinario. */
    private Long medicoId;
    /** Nombre completo del veterinario. */
    private String medicoNombre;
    /** Fecha y hora de la anotación. */
    private LocalDateTime fecha;
    /** Descripción de lo ocurrido. */
    private String descripcion;
    /** Observación del veterinario. */
    private String observacion;
    /** Diagnóstico o evolución del paciente. */
    private String diagnosticoEvolucion;
}
