package com.uniminuto.clinica.models;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Datos de entrada para agregar un lote a un medicamento existente
 * (reabastecimiento del inventario).
 */
@Data
public class AgregarLoteRq {

    private Long medicamentoId;
    private String numeroLote;
    private Integer cantidad;
    private BigDecimal precioCompra;
    private LocalDate fechaIngreso;
    private LocalDate fechaVencimiento;
}
