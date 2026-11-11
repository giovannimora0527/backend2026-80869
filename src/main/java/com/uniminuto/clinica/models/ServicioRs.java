package com.uniminuto.clinica.models;

import lombok.Data;

import java.math.BigDecimal;

/**
 * Datos de salida de un servicio, incluyendo el nombre de su especializacion
 * (si aplica) y el precio con IVA ya calculado como referencia.
 */
@Data
public class ServicioRs {

    private Long id;
    private String nombre;
    private String descripcion;
    private Long especializacionId;
    private String especializacionNombre;
    private BigDecimal precio;
    private BigDecimal ivaPorcentaje;

    /** Precio con IVA incluido (precio + precio * iva / 100), como referencia. */
    private BigDecimal precioConIva;

    private Boolean activo;
}
