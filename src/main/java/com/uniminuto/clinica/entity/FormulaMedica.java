package com.uniminuto.clinica.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * Entidad que representa una fórmula médica en la base de datos.
 * @author Yulian Montealegre
 */
@Entity
@Table(name = "formula_medica")
@Data
public class FormulaMedica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "cita_id", nullable = false)
    private Long citaId;

    @Column(name = "medicamento_id", nullable = false)
    private Long medicamentoId;

    @Column(name = "nombre_medicamento")
    private String nombreMedicamento;

    @Column(name = "presentacion")
    private String presentacion;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "dosis", nullable = false, columnDefinition = "TEXT")
    private String dosis;

    @Column(name = "indicaciones", columnDefinition = "TEXT")
    private String indicaciones;

    @Column(name = "fecha_creacion_registro", nullable = false)
    private LocalDateTime fechaCreacionRegistro;

    @Column(name = "fecha_actualizacion_registro")
    private LocalDateTime fechaActualizacionRegistro;
}