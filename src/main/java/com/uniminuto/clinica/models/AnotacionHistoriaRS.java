package com.uniminuto.clinica.models;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * (response) que representa una anotación de historia médica.
 */
@Data
public class AnotacionHistoriaRS {


    private Long id;


    private Integer historiaId;


    private Integer medicoId;


    private LocalDateTime fecha;


    private String descripcion;
}