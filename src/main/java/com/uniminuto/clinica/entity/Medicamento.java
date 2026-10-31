package com.uniminuto.clinica.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Medicamento disponible en la clínica para recetar en las fórmulas médicas.
 * Nunca se borra físicamente: se desactiva con el campo {@code activo}, así las
 * fórmulas ya emitidas conservan su referencia.
 */
@Entity
@Table(name = "medicamento")
@Data
public class Medicamento {

    /** Identificador único del medicamento. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    /** Nombre del medicamento; no puede repetirse. */
    @Column(name = "nombre", nullable = false, length = 100, unique = true)
    private String nombre;

    /** Descripción o uso general; es opcional. */
    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    /** Presentación comercial; es opcional. */
    @Column(name = "presentacion", length = 100)
    private String presentacion;

    /** Fecha de compra del lote. */
    @Column(name = "fecha_compra", nullable = false)
    private LocalDate fechaCompra;

    /** Fecha de vencimiento del lote. */
    @Column(name = "fecha_vence", nullable = false)
    private LocalDate fechaVence;

    /** Fecha y hora en que se registró en el sistema. */
    @Column(name = "fecha_creacion_registro", nullable = false)
    private LocalDateTime fechaCreacionRegistro;

    /** Fecha y hora de la última modificación; vacío si nunca se editó. */
    @Column(name = "fecha_modificacion_registro")
    private LocalDateTime fechaModificacionRegistro;

    /**
     * Indica si el medicamento se puede recetar (borrado lógico).
     * Requiere la columna creada por el script {@code 01_alter_medicamento_activo.sql}.
     */
    @Column(name = "activo", nullable = false)
    private Boolean activo = Boolean.TRUE;
}
