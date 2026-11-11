package com.uniminuto.clinica.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Registro (log) del historial de cambios de tarifa de un servicio.
 *
 * Cada vez que cambia el precio o el porcentaje de IVA de un servicio, se
 * guarda el valor anterior y el nuevo, con la fecha y el usuario responsable.
 * Esto garantiza la trazabilidad de las tarifas exigida por el enunciado.
 */
@Entity
@Table(name = "log_tarifa")
@Data
public class LogTarifa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "servicio_id", nullable = false)
    private Long servicioId;

    @Column(name = "precio_anterior", precision = 12, scale = 2)
    private BigDecimal precioAnterior;

    @Column(name = "precio_nuevo", precision = 12, scale = 2)
    private BigDecimal precioNuevo;

    @Column(name = "iva_anterior", precision = 5, scale = 2)
    private BigDecimal ivaAnterior;

    @Column(name = "iva_nuevo", precision = 5, scale = 2)
    private BigDecimal ivaNuevo;

    @Column(name = "fecha", nullable = false)
    private LocalDateTime fecha;

    @Column(name = "usuario_responsable", length = 100)
    private String usuarioResponsable;
}
