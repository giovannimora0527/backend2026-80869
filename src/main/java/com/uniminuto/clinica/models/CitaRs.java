package com.uniminuto.clinica.models;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * Datos de salida de una cita, enriquecidos para la vista de calendario y los
 * listados. Incluye la informacion del paciente (mascota), su propietario y el
 * medico, ademas del estado y el motivo.
 */
@Data
public class CitaRs {

    private Long id;
    private LocalDateTime fechaHora;
    private LocalDateTime fechaHoraFin;
    private String estado;
    private String motivo;

    private Long mascotaId;
    private String mascotaNombre;

    private Long clienteId;
    private String clienteNombre;
    private String clienteTelefono;

    private Long medicoId;
    private String medicoNombre;
    private String especializacion;
}
