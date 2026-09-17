package com.uniminuto.clinica.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entidad de dominio que representa una anotación en la historia médica.
 */
@Entity
@Table(name = "anotacion_historia")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnotacionHistoria {

    /**
     * Identificador único autogenerado de la anotación.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * Contenido textual u observaciones médicas.
     */
    @Column(name = "observacion", nullable = false, columnDefinition = "TEXT")
    private String observacion;

    /**
     * Fecha y hora en la que se creó el registro.
     */
    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    /**
     * Método ejecutado antes de persistir para asegurar la fecha de creación.
     */
    @PrePersist
    public void prePersist() {
        if (this.fechaCreacion == null) {
            this.fechaCreacion = LocalDateTime.now();
        }
    }
}