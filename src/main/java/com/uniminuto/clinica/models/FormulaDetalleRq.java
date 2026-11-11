package com.uniminuto.clinica.models;

import lombok.Data;

/**
 * Datos de entrada de un medicamento dentro de una formula (detalle).
 */
@Data
public class FormulaDetalleRq {

    private Long medicamentoId;
    private String dosis;
    private String frecuencia;
    private String duracion;
    private Integer cantidad;
    private String viaAdministracion;
    private String indicaciones;
}
