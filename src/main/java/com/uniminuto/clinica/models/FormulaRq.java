package com.uniminuto.clinica.models;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * Datos que envía el frontend para crear o editar una fórmula médica.
 */
@Data
public class FormulaRq {

    /** Identificador de la fórmula (solo al editar). */
    private Long id;

    /** Consulta (cita) a la que pertenece la fórmula. */
    private Long citaId;

    /** Mascota a la que se formula; debe ser la de la cita. */
    private Integer mascotaId;

    /** Veterinario que emite la fórmula. */
    private Long medicoId;

    /** Medicamento recetado. */
    private Long medicamentoId;

    /** Dosis: un número mayor a cero con su unidad (por ejemplo "0.5 ml"). */
    private String dosis;

    /** Cada cuánto se administra (por ejemplo "Cada 12 horas"). */
    private String frecuencia;

    /** Duración del tratamiento: un número mayor a cero con su unidad (por ejemplo "5 días"). */
    private String duracion;

    /** Indicaciones adicionales para el cliente. */
    private String indicaciones;

    /** Fecha de emisión; si no llega se usa la fecha y hora actuales. */
    private LocalDateTime fechaEmision;
}
