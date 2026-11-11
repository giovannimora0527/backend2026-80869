package com.uniminuto.clinica.models;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * Datos de entrada para generar una factura a partir de una cita, con sus
 * items (servicios y medicamentos) y un descuento opcional.
 */
@Data
public class FacturaRq {

    private Long citaId;
    private BigDecimal descuento;
    private String observacion;
    private List<FacturaDetalleRq> items;
}
