package com.uniminuto.clinica.models;

import lombok.Data;

import java.math.BigDecimal;

/**
 * Datos de entrada para crear o actualizar un servicio.
 */
@Data
public class ServicioRq {

    private Long id;
    private String nombre;
    private String descripcion;

    /** Especializacion asociada (opcional). Null para servicios generales. */
    private Long especializacionId;

    private BigDecimal precio;
    private BigDecimal ivaPorcentaje;
    private Boolean activo;
}
