package com.uniminuto.clinica.models;

import lombok.Data;

/**
 * Datos de entrada para cambiar el estado de una cita
 * (confirmar, marcar atendida, cancelar, finalizar).
 */
@Data
public class CambiarEstadoCitaRq {

    private Long citaId;

    /** Nuevo estado: CONFIRMADA, ATENDIDA, CANCELADA, FINALIZADA. */
    private String estado;
}
