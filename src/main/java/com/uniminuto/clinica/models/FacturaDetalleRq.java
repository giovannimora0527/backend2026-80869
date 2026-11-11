package com.uniminuto.clinica.models;

import lombok.Data;

/**
 * Item de entrada para generar una factura. Indica el tipo (SERVICIO o
 * MEDICAMENTO), el identificador referenciado y la cantidad.
 */
@Data
public class FacturaDetalleRq {

    private String tipo;
    private Long referenciaId;
    private Integer cantidad;
}
