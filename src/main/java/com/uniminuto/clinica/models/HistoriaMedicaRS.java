package com.uniminuto.clinica.models;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * (response) que representa una historia médica.
 */
@Data
public class HistoriaMedicaRS {


    private Long id;


    private Integer pacienteId;


    private LocalDateTime fechaCreacion;
}