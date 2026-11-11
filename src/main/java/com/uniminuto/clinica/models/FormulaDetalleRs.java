package com.uniminuto.clinica.models;

import lombok.Data;

/**
 * Datos de salida de un medicamento dentro de una formula (detalle).
 */
@Data
public class FormulaDetalleRs {

    private Long id;
    private Long medicamentoId;
    private String medicamentoNombre;
    private String dosis;
    private String frecuencia;
    private String duracion;
    private Integer cantidad;
    private String viaAdministracion;
    private String indicaciones;
}
