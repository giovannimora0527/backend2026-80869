package com.uniminuto.clinica.models;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class MedicamentoLoteRq {

    private String numeroLote;
    private LocalDate fechaCompra;
    private LocalDate fechaVence;
    private BigDecimal precioCompra;
    private Integer cantidadIngresada;
}