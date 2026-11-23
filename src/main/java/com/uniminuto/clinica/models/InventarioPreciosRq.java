package com.uniminuto.clinica.models;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class InventarioPreciosRq {
    private BigDecimal precioVenta;
    private Integer stockMinimo;
}