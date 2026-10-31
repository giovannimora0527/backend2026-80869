package com.uniminuto.clinica.models;

import lombok.Data;

/**
 * Datos para agregar una anotación a una historia clínica existente.
 * Se debe indicar la historia ({@code historiaId}) o, en su defecto, la cita.
 */
@Data
public class AnotacionHistoriaRq {

    /** Cita de la consulta (opcional si se envía la historia). */
    private Long citaId;

    /** Historia clínica a la que se agrega la anotación. */
    private Long historiaId;

    /** Veterinario que escribe la anotación. */
    private Long medicoId;

    /** Descripción de lo realizado en la consulta. */
    private String descripcion;

    /** Diagnóstico o evolución. */
    private String diagnostico;

    /** Observaciones. */
    private String observaciones;
}
