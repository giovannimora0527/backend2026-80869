package com.uniminuto.clinica.models;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * DTO para la solicitud de creación de una anotación de historia.
 *
 * @author Yulian Estephania
 * @version 1.0
 */
@Data
public class AnotacionHistoriaRq {

    @NotNull(message = "El ID de la historia es obligatorio")
    private Long historiaId;

    @NotNull(message = "El ID del médico es obligatorio")
    private Long medicoId;

    @NotNull(message = "La descripción es obligatoria")
    private String descripcion;

    private String observacion;

    private String diagnosticoEvolucion;
}