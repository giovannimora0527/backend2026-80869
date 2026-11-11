package com.uniminuto.clinica.models;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Datos de salida de un registro del historial de cambios de tarifa.
 */
@Data
public class LogTarifaRs {

    private Long id;
    private Long servicioId;
    private BigDecimal precioAnterior;
    private BigDecimal precioNuevo;
    private BigDecimal ivaAnterior;
    private BigDecimal ivaNuevo;
    private LocalDateTime fecha;
    private String usuarioResponsable;
}
