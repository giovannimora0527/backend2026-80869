package com.uniminuto.clinica.models;

import lombok.Data;

import java.math.BigDecimal;

/**
 * Item de salida de una factura.
 */
@Data
public class FacturaDetalleRs {

    private Long id;
    private String tipo;
    private Long referenciaId;
    private String descripcion;
    private Integer cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal ivaPorcentaje;
    private BigDecimal subtotal;
}
