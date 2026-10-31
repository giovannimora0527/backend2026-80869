package com.uniminuto.clinica.models;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * Datos que envía el frontend para crear o editar una cita.
 */
@Data
public class CitaRq {

    /** Identificador de la cita (solo al editar). */
    private Long id;

    /** Cliente dueño de la mascota. Si no llega se toma el de la mascota. */
    private Long clienteId;

    /** Mascota que será atendida; debe pertenecer al cliente. */
    private Integer mascotaId;

    /** Veterinario que atenderá. */
    private Long medicoId;

    /** Fecha y hora de la cita. */
    private LocalDateTime fechaHora;

    /** Motivo de la consulta. */
    private String motivo;

    /** Estado de la cita; si no llega se usa PROGRAMADA al crear. */
    private String estado;
}
