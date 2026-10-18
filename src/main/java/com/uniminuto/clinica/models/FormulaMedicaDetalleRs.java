package com.uniminuto.clinica.models;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/** Detalle completo de una fórmula médica (incluye los campos que el listado omite). */
@Data
public class FormulaMedicaDetalleRs {
    /** Identificador de la fórmula. */
    private Long id;
    /** Cita (consulta) asociada. */
    private Long citaId;
    /** Fecha y hora de la cita asociada. */
    private LocalDateTime citaFechaHora;
    /** Historia clínica asociada (puede ser nula). */
    private Long historiaId;
    /** Identificador de la mascota. */
    private Integer mascotaId;
    /** Nombre de la mascota. */
    private String mascotaNombre;
    /** Identificador del cliente. */
    private Long clienteId;
    /** Nombre completo del cliente. */
    private String clienteNombre;
    /** Identificador del veterinario. */
    private Long medicoId;
    /** Nombre completo del veterinario. */
    private String medicoNombre;
    /** Identificador del medicamento. */
    private Long medicamentoId;
    /** Nombre del medicamento. */
    private String medicamentoNombre;
    /** Presentación del medicamento. */
    private String presentacion;
    /** Descripción del medicamento. */
    private String descripcion;
    /** Fecha de emisión. */
    private LocalDate fechaEmision;
    /** Dosis por toma. */
    private String dosis;
    /** Frecuencia de administración. */
    private String frecuencia;
    /** Duración del tratamiento en días. */
    private Integer duracionDias;
    /** Indicaciones para el propietario. */
    private String indicaciones;
    /** {@code true} si la fórmula está activa. */
    private Boolean activo;
    /** Fecha de creación del registro. */
    private LocalDateTime fechaCreacionRegistro;
    /** Fecha de la última actualización. */
    private LocalDateTime fechaActualizacionRegistro;
}
