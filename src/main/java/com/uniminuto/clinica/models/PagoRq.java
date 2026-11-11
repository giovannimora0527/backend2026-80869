package com.uniminuto.clinica.models;

import lombok.Data;

import java.math.BigDecimal;

/**
 * Datos de entrada para registrar un pago sobre una factura.
 */
@Data
public class PagoRq {

    private Long facturaId;
    private BigDecimal monto;
    private String metodo;
}
