package com.uniminuto.clinica.models;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * (response) que representa una fórmula médica del inventario.
 */
@Data
public class FormulaMedicaRS {


    private Long id;


    private Integer citaId;


    private Integer medicamentoId;


    private String dosis;


    private String indicaciones;


    private LocalDateTime fechaCreacionRegistro;


    private LocalDateTime fechaActualizacionRegistro;
}