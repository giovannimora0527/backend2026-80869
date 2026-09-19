package com.uniminuto.clinica.models;

import lombok.Data;

/**
 * (request) para crear o actualizar una historia médica.
 */
@Data
public class HistoriaMedicaRQ {

    /**
     * id para actualizar, si es para crear no se necesita id
     */
    private Long id;


    private Integer pacienteId;
}