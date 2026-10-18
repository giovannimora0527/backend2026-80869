package com.uniminuto.clinica.models;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/** Datos resumidos de una fórmula médica para el listado. */
@Data
public class FormulaMedicaResumenRs {
    /** Identificador de la fórmula. */
    private Long id;
    /** Fecha de emisión. */
    private LocalDate fechaEmision;
    /** Identificador de la mascota. */
    private Integer mascotaId;
    /** Nombre de la mascota. */
    private String mascotaNombre;
    /** Nombre completo del cliente propietario. */
    private String clienteNombre;
    /** Nombre completo del veterinario. */
    private String medicoNombre;
    /** Nombre del medicamento. */
    private String medicamentoNombre;
    /** Dosis por toma. */
    private String dosis;
    /** Frecuencia de administración. */
    private String frecuencia;
    /** Duración del tratamiento en días. */
    private Integer duracionDias;
    /** {@code true} si la fórmula está activa. */
    private Boolean activo;
    /** Fecha de creación del registro (criterio de orden inicial). */
    private LocalDateTime fechaCreacionRegistro;
}
