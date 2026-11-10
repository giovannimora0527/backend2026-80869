package com.uniminuto.clinica.models;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * Datos de entrada para reprogramar una cita (cambiar su fecha y hora).
 */
@Data
public class ReprogramarCitaRq {

    private Long citaId;
    private LocalDateTime nuevaFechaHora;
}
