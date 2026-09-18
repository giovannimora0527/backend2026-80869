package com.uniminuto.clinica.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * Entidad que representa una formula medica en el sistema.
 */
@Entity
@Table(name = "formula_medica")
@Data
public class FormulaMedica {

    /** Identificador unico de la formula medica. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /** ID de la cita asociada. */
    @Column(name = "cita_id", nullable = false)
    private Integer citaId;

    /** ID del medicamento asociado. */
    @Column(name = "medicamento_id", nullable = false)
    private Integer medicamentoId;

    /** Dosis del medicamento. */
    @Column(name = "dosis", columnDefinition = "TEXT", nullable = false)
    private String dosis;

    /** Indicaciones de uso. */
    @Column(name = "indicaciones", columnDefinition = "TEXT")
    private String indicaciones;

    /** Fecha de creacion del registro. */
    @Column(name = "fecha_creacion_registro", nullable = false)
    private LocalDateTime fechaCreacionRegistro;

    /** Fecha de actualizacion del registro. */
    @Column(name = "fecha_actualizacion_registro")
    private LocalDateTime fechaActualizacionRegistro;

    /** Asigna la fecha actual si no se proporciona. */
    @PrePersist
    public void prePersist() {
        if (this.fechaCreacionRegistro == null) {
            this.fechaCreacionRegistro = LocalDateTime.now();
        }
    }
}