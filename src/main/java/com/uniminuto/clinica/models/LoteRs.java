package com.uniminuto.clinica.models;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Datos de salida de un lote de medicamento, incluyendo indicadores de
 * vencimiento calculados.
 */
@Data
public class LoteRs {

    private Long id;
    private String numeroLote;
    private Integer cantidad;
    private BigDecimal precioCompra;
    private LocalDate fechaIngreso;
    private LocalDate fechaVencimiento;

    /** Indica si el lote ya esta vencido a la fecha actual. */
    private Boolean vencido;

    /** Indica si el lote esta proximo a vencer (dentro del umbral definido). */
    private Boolean proximoAVencer;
}
