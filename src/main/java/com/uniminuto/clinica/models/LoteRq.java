package com.uniminuto.clinica.models;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Datos de entrada de un lote de medicamento.
 */
@Data
public class LoteRq {

    private Long id;
    private String numeroLote;
    private Integer cantidad;
    private BigDecimal precioCompra;
    private LocalDate fechaIngreso;
    private LocalDate fechaVencimiento;
}
