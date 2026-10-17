package com.uniminuto.clinica.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excepción lanzada cuando ocurre un conflicto de negocio, como solapamiento de horarios de citas.
 * Retorna un código HTTP 409 Conflict.
 *
 * @author Yulian Estephania Montealegre
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class ConflictException extends RuntimeException {

    /**
     * Constructor con mensaje personalizado.
     *
     * @param message Mensaje descriptivo del conflicto.
     */
    public ConflictException(String message) {
        super(message);
    }
}