package com.uniminuto.clinica.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Historia clínica: registro de una consulta realizada a una mascota.
 * Una mascota puede tener muchas historias (una por consulta) y cada historia
 * puede tener varias anotaciones y fórmulas. Cada cita atendida tiene como máximo una historia.
 */
@Entity
@Table(name = "historia_medica")
@Data
public class HistoriaMedica {

    /** Identificador único de la historia. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    /** Mascota atendida (de ella se obtiene el cliente). */
    @ManyToOne
    @JoinColumn(name = "paciente_id", nullable = false)
    private Mascota mascota;

    /** Fecha en que se creó el registro en el sistema. */
    @Column(name = "fecha_creacion", columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime fechaCreacion;

    /** Cita que originó la consulta. */
    @ManyToOne
    @JoinColumn(name = "cita_id")
    private Cita cita;

    /** Veterinario que atendió la consulta. */
    @ManyToOne
    @JoinColumn(name = "medico_id")
    private Medico medico;

    /** Fecha y hora de la consulta. */
    @Column(name = "fecha_consulta")
    private LocalDateTime fechaConsulta;

    /** Motivo de la consulta. */
    @Column(name = "motivo", columnDefinition = "TEXT")
    private String motivo;

    /** Diagnóstico del veterinario. */
    @Column(name = "diagnostico", columnDefinition = "TEXT")
    private String diagnostico;

    /** Observaciones adicionales. */
    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;
}
