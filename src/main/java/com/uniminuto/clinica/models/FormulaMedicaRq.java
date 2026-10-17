package com.uniminuto.clinica.models;

import lombok.Data;

/**
 * Objeto de transferencia de datos (DTO) para la creación y actualización de fórmulas médicas.
 *
 * @author Yulian Estephania Montealegre
 */
@Data
public class FormulaMedicaRq {

    /** Identificador de la fórmula médica (usado en actualización). */
    private Long id;

    /** Identificador obligatorio de la cita médica asociada. */
    private Long citaId;

    /** Identificador obligatorio del medicamento recetado. */
    private Long medicamentoId;

    /** Nombre del medicamento (opcional si se consulta del catálogo). */
    private String nombreMedicamento;

    /** Presentación del medicamento (ej: Jarabe 100ml, Pastillas 500mg). */
    private String presentacion;

    /** Descripción detallada del tratamiento o contraindicaciones. */
    private String descripcion;

    /** Dosis prescrita por el veterinario (ej: 1 pastilla cada 8 horas). */
    private String dosis;

    /** Indicaciones adicionales para el propietario de la mascota. */
    private String indicaciones;
}