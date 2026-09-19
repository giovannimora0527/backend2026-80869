package com.uniminuto.clinica.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Entidad que representa la historia médica de un paciente (mascota).
 * Mapea la tabla (historia_medica). Se relaciona lógicamente con
 * (AnotacionHistoria) (una historia puede tener varias anotaciones),
 * pero esa relación se maneja mediante el campo (code historiaId) dentro de
 * (AnotacionHistoria), en lugar de una colección @OneToMany, para
 * mantener el modelo simple
 */
@Data
@Entity
@Table(name = "historia_medica")
public class HistoriaMedica {

    /** Identificador único de la historia médica (PK, autoincremental). */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /** Identificador del paciente (mascota) dueño de esta historia médica. */
    @Column(name = "paciente_id", nullable = false)
    private Integer pacienteId;


    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;
}