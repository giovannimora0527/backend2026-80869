package com.uniminuto.clinica.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Fórmula médica emitida por un veterinario para una mascota en una consulta.
 * Nunca se borra físicamente: se inactiva con {@code activo}, así el historial
 * clínico conserva lo que se recetó.
 */
@Entity
@Table(name = "formula_medica")
@Data
public class FormulaMedica {

    /** Identificador único de la fórmula. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    /** Consulta (cita) a la que pertenece la fórmula. */
    @ManyToOne
    @JoinColumn(name = "cita_id", nullable = false)
    private Cita cita;

    /** Mascota a la que se formula (debe ser la de la cita). */
    @ManyToOne
    @JoinColumn(name = "mascota_id", nullable = false)
    private Mascota mascota;

    /** Veterinario que emite la fórmula. */
    @ManyToOne
    @JoinColumn(name = "medico_id", nullable = false)
    private Medico medico;

    /** Historia clínica de la consulta, si ya existe. */
    @ManyToOne
    @JoinColumn(name = "historia_id")
    private HistoriaMedica historia;

    /** Medicamento recetado. */
    @ManyToOne
    @JoinColumn(name = "medicamento_id", nullable = false)
    private Medicamento medicamento;

    /** Dosis (número mayor a cero + unidad). */
    @Column(name = "dosis", nullable = false, columnDefinition = "TEXT")
    private String dosis;

    /** Indicaciones adicionales. */
    @Column(name = "indicaciones", columnDefinition = "TEXT")
    private String indicaciones;

    /** Fecha y hora de emisión; no puede ser anterior a la consulta. */
    @Column(name = "fecha_emision", nullable = false)
    private LocalDateTime fechaEmision;

    /** Fecha y hora en que se registró en el sistema. */
    @Column(name = "fecha_creacion_registro", nullable = false)
    private LocalDateTime fechaCreacionRegistro;

    /** Fecha y hora de la última modificación. */
    @Column(name = "fecha_actualizacion_registro")
    private LocalDateTime fechaActualizacionRegistro;

    /** Cada cuánto se administra. */
    @Column(name = "frecuencia")
    private String frecuencia;

    /** Duración del tratamiento. */
    @Column(name = "duracion")
    private String duracion;

    /** Borrado lógico: {@code false} = fórmula inactiva. */
    @Column(name = "activo", nullable = false)
    private Boolean activo = Boolean.TRUE;
}
