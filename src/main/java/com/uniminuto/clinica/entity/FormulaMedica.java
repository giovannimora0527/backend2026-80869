package com.uniminuto.clinica.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "formula_medica")
@Data
public class FormulaMedica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cita_id", nullable = false)
    private Cita cita;

    /**
     * Medicamento unico (modelo antiguo). Ahora es opcional: las formulas nuevas
     * usan el detalle formula_medicamento para soportar varios medicamentos.
     */
    @ManyToOne
    @JoinColumn(name = "medicamento_id")
    private Medicamento medicamento;

    /** Medico que genera la formula (cabecera). */
    @Column(name = "medico_id")
    private Long medicoId;

    /** Fecha de creacion de la formula (cabecera). */
    @Column(name = "fecha_creacion")
    private java.time.LocalDateTime fechaCreacion;

    /** Dosis del modelo antiguo (opcional). En el modelo nuevo va en el detalle. */
    @Column(name = "dosis", columnDefinition = "TEXT")
    private String dosis;

    @Column(name = "indicaciones", columnDefinition = "TEXT")
    private String indicaciones;

    @Column(name = "fecha_creacion_registro", nullable = false)
    private LocalDateTime fechaCreacionRegistro;

    @Column(name = "fecha_actualizacion_registro")
    private LocalDateTime fechaActualizacionRegistro;

    @Column(name = "frecuencia")
    private String frecuencia;

    @Column(name = "duracion")
    private String duracion;
}
