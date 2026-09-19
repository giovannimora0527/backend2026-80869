package com.uniminuto.clinica.models;

import lombok.Data;

/**
 * (request) para crear o actualizar una anotación de
 * historia médica.
 */
@Data
public class AnotacionHistoriaRQ {

    /**
     * si es para crear no se necesita id, si es para actualizar se necesita el id de la anotación
     */
    private Long id;


    private Integer historiaId;


    private Integer medicoId;


    private String descripcion;
}