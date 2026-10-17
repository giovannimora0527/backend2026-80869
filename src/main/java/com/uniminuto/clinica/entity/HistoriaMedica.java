package com.uniminuto.clinica.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Representa la entidad Historia Médica en la base de datos de la clínica veterinaria.
 * Almacena el historial clínico detallado de las atenciones brindadas a las mascotas,
 * vinculando la cita, el paciente, el médico tratante y los detalles diagnósticos.
 *
 * @author Yulian Estefania
 * @version 1.0
 */
@Entity
@Table(name = "historia_medica")
@Data
public class HistoriaMedica {

    /**
     * Identificador único de la historia médica (Clave primaria autoincremental).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    /**
     * Mascota (paciente) asociada a esta historia médica.
     */
    @ManyToOne
    @JoinColumn(name = "paciente_id", nullable = false)
    private Mascota mascota;

    /**
     * Cita médica que originó la atención y generación de la historia clínica.
     */
    @ManyToOne
    @JoinColumn(name = "cita_id", nullable = false)
    private Cita cita;

    /**
     * Médico veterinario responsable de la atención y diagnóstico.
     */
    @ManyToOne
    @JoinColumn(name = "medico_id", nullable = false)
    private Medico medico;

    /**
     * Fecha y hora en la que se llevó a cabo la consulta.
     */
    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;

    /**
     * Motivo principal de la consulta reportado por el propietario.
     */
    @Column(name = "motivo_consulta", nullable = false, columnDefinition = "TEXT")
    private String motivoConsulta;

    /**
     * Diagnóstico clínico emitido por el médico veterinario.
     */
    @Column(name = "diagnostico", nullable = false, columnDefinition = "TEXT")
    private String diagnostico;

    /**
     * Observaciones adicionales, recomendaciones o tratamientos recetados.
     */
    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;

    /**
     * Estado actual del registro de la historia médica (Ej. ACTIVA, CERRADA).
     */
    @Column(name = "estado", length = 20)
    private String estado;

    /**
     * Fecha y hora exacta en la que se creó el registro en el sistema.
     */
    @Column(name = "fecha_creacion", columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime fechaCreacion;
}