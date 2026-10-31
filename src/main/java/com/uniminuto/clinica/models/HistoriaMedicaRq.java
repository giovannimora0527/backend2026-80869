package com.uniminuto.clinica.models;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * Datos que envía el frontend para registrar o editar una historia clínica
 * (una consulta realizada a una mascota).
 */
@Data
public class HistoriaMedicaRq {

    /** Identificador de la historia (solo al editar). */
    private Long id;

    /** Cliente dueño de la mascota. */
    private Long clienteId;

    /** Mascota atendida; debe pertenecer al cliente. */
    private Integer mascotaId;

    /** Cita que originó la consulta; debe ser de esa mascota. */
    private Long citaId;

    /** Veterinario que atendió; debe ser el de la cita. */
    private Long medicoId;

    /** Fecha y hora de la consulta. */
    private LocalDateTime fechaHora;

    /** Motivo de la consulta. */
    private String motivo;

    /** Diagnóstico del veterinario. */
    private String diagnostico;

    /** Observaciones adicionales. */
    private String observaciones;
}
