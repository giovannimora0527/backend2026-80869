package com.uniminuto.clinica.models;

import lombok.Data;

/**
 * Modelo estandarizado para las respuestas HTTP de la API.
 *
 * @author Yulian Estephania Montealegre
 * @version 1.0
 */
@Data
public class MiRespuestaRS {

    private int status;
    private String message;
    private Object data; // Campo para retornar el objeto creado o consultado
}