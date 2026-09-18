package com.uniminuto.clinica.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * Entidad que representa una anotacion dentro de la historia medica.
 * Cada anotacion pertenece a una historia medica especifica.
 */
@Entity
@Table(name = "anotacion_historia")
@Data
public class AnotacionHistoria {

    /**
     * Identificador unico de la anotacion.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * Detalle o contenido de la anotacion.
     */
    @Column(name = "detalle", nullable = false, columnDefinition = "TEXT")
    private String detalle;

    /**
     * Fecha y hora en que se creo la anotacion.
     */
    @Column(name = "fecha", nullable = false)
    private LocalDateTime fecha;

    /**
     * Historia medica a la que pertenece esta anotacion.
     * ManyToOne indica que muchas anotaciones pueden pertenecer a una sola historia.
     */
    @ManyToOne
    @JoinColumn(name = "historia_medica_id", nullable = false)
    private HistoriaMedica historiaMedica;

    /**
     * Asigna automaticamente la fecha y hora actual antes de insertar en la BD
     * en caso de que el cliente no la envie.
     */
    @PrePersist
    public void prePersist() {
        if (this.fecha == null) {
            this.fecha = LocalDateTime.now();
        }
    }
}