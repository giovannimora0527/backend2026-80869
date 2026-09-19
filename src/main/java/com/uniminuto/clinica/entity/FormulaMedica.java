package com.uniminuto.clinica.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Mapea la tabla formula_medica
 */
@Data
@Entity
@Table(name = "formula_medica")
public class FormulaMedica {

    /** Identificador único de la fórmula médica (PK, autoincrementable). */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /** Identificador de la cita asociada a esta fórmula. */
    @Column(name = "cita_id", nullable = false)
    private Integer citaId;

    /** Identificador del medicamento formulado. */
    @Column(name = "medicamento_id", nullable = false)
    private Integer medicamentoId;


    @Column(name = "dosis", nullable = false, columnDefinition = "TEXT")
    private String dosis;


    @Column(name = "indicaciones", columnDefinition = "TEXT")
    private String indicaciones;


    @Column(name = "fecha_creacion_registro", nullable = false)
    private LocalDateTime fechaCreacionRegistro;

    /** (si aplica). */
    @Column(name = "fecha_actualizacion_registro")
    private LocalDateTime fechaActualizacionRegistro;
}