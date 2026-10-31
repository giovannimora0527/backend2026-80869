package com.uniminuto.clinica.models;

import lombok.Data;

/** Solicitud para cambiar solo el estado de una cita (PATCH). */
@Data
public class CambioEstadoCitaRq {

    /** Nuevo estado: PENDIENTE, PROGRAMADA, CONFIRMADA, ATENDIDA o CANCELADA. */
    private String estado;
}
