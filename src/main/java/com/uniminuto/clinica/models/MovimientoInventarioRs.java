package com.uniminuto.clinica.models;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * Datos de salida de un movimiento de inventario (entrada o salida).
 */
@Data
public class MovimientoInventarioRs {

    private Long id;
    private Long medicamentoId;
    private String medicamentoNombre;
    private Long loteId;
    private String tipo;
    private Integer cantidad;
    private LocalDateTime fecha;
    private String usuarioResponsable;
    private String referencia;
    private String observacion;
}
