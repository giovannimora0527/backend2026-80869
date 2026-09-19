package com.uniminuto.clinica.models;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * (request) para crear o actualizar una cita.
 */
@Data
public class CitaRQ {

    /**
     * se necesita id para actualizar, si es para crear no se necesita id
     */
    private Long id;


    private Integer clienteId;


    private Integer mascotaId;


    private Integer medicoId;


    private LocalDateTime fechaHora;


    private String estado;


    private String motivo;
}