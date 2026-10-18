package com.uniminuto.clinica.models;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/** Representación de una cita para listados y detalle. */
@Data
public class CitaRs {
    /** Identificador de la cita. */
    private Long id;
    /** Fecha y hora completas. */
    private LocalDateTime fechaHora;
    /** Solo la fecha (para columnas del listado). */
    private LocalDate fecha;
    /** Solo la hora (para columnas del listado). */
    private LocalTime hora;
    /** Identificador del cliente. */
    private Long clienteId;
    /** Nombre completo del cliente. */
    private String clienteNombre;
    /** Identificador de la mascota. */
    private Integer mascotaId;
    /** Nombre de la mascota. */
    private String mascotaNombre;
    /** Identificador del veterinario. */
    private Long medicoId;
    /** Nombre completo del veterinario. */
    private String medicoNombre;
    /** Estado de la cita. */
    private String estado;
    /** Motivo de la consulta. */
    private String motivo;
    /** Historia clínica asociada; solo se informa en el detalle (en el listado es nulo). */
    private Long historiaId;
}
