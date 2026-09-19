package com.uniminuto.clinica.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Entidad que representa una anotación dentro de una historia médica.
 * Mapea la tabla (anotacion_historia), la cual se relaciona con
 * (historia_medica) mediante el campo (historiaId).
 */
@Data
@Entity
@Table(name = "anotacion_historia")
public class AnotacionHistoria {

    /** id clave primaria, identity (autoincrementable)*/
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /** Identificador de la historia médica a la que pertenece esta anotación. (llaves foraneas que s econectan a otras tablas)*/
    @Column(name = "historia_id", nullable = false)
    private Integer historiaId;

    /** Identificador del médico que realizó la anotación. */
    @Column(name = "medico_id", nullable = false)
    private Integer medicoId;


    @Column(name = "fecha")
    private LocalDateTime fecha;


    @Column(name = "descripcion", nullable = false, columnDefinition = "TEXT")
    private String descripcion;
}