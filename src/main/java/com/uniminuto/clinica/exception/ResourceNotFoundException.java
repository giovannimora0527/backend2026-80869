package com.uniminuto.clinica.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excepción lanzada cuando no se encuentra un recurso solicitado en la base de datos.
 * Retorna automáticamente un código HTTP 404 Not Found al cliente.
 *
 * @author Yulian Estefanía Montealegre
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

    /**
     * Constructor con mensaje descriptivo del recurso faltante.
     *
     * @param message Mensaje indicando cuál recurso no fue hallado.
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }
}