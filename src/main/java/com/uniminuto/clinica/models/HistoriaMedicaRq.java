package com.uniminuto.clinica.models;

import jakarta.validation.constraints.NotNull; // <-- Cambia javax por jakarta aquí
import java.time.LocalDateTime;
import lombok.Data;

/**
 * Clase que representa la solicitud (Request DTO) para el registro de una Historia Médica.
 *
 * @author Yulian Estephania
 * @version 1.0
 */
@Data
public class HistoriaMedicaRq {

    @NotNull(message = "El ID del cliente es obligatorio")
    private Long clienteId;

    @NotNull(message = "El ID de la mascota es obligatorio")
    private Long mascotaId;

    @NotNull(message = "El ID de la cita es obligatorio")
    private Long citaId;

    @NotNull(message = "El ID del médico es obligatorio")
    private Long medicoId;

    @NotNull(message = "La fecha y hora son obligatorias")
    private LocalDateTime fechaHora;

    @NotNull(message = "El motivo de consulta es obligatorio")
    private String motivoConsulta;

    @NotNull(message = "El diagnóstico es obligatorio")
    private String diagnostico;

    private String observaciones;
}