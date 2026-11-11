package com.uniminuto.clinica.models;

import lombok.Data;

import java.util.List;

/**
 * Datos de entrada para crear o actualizar una formula medica con varios
 * medicamentos. La cabecera referencia la cita y el medico; el detalle lista
 * los medicamentos prescritos.
 */
@Data
public class FormulaNuevaRq {

    private Long id;
    private Long citaId;
    private Long medicoId;
    private String indicaciones;

    /** Medicamentos prescritos en la formula. */
    private List<FormulaDetalleRq> medicamentos;
}
