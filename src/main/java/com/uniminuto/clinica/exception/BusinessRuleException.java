package com.uniminuto.clinica.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excepción lanzada cuando se incumple una regla de negocio de la veterinaria.
 * Retorna un código HTTP 422 Unprocessable Entity.
 *
 * @author Yulian Estephania Montealegre
 */
@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
public class BusinessRuleException extends RuntimeException {

    /**
     * Constructor con mensaje personalizado.
     *
     * @param message Mensaje descriptivo de la regla incumplida.
     */
    public BusinessRuleException(String message) {
        super(message);
    }
}