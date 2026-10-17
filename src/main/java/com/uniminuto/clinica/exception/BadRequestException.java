package com.uniminuto.clinica.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excepción personalizada para manejar errores de solicitud incorrecta (Bad Request).
 * Retorna automáticamente un código HTTP 400 Bad Request al cliente.
 *
 * @author Yulian Estefanía Montealegre
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BadRequestException extends RuntimeException {

    private final HttpStatus status;

    /**
     * Constructor con mensaje descriptivo del error.
     *
     * @param message Mensaje explicativo del error de validación.
     */
    public BadRequestException(String message) {
        super(message);
        this.status = HttpStatus.BAD_REQUEST;
    }

    /**
     * Obtiene el estado HTTP asociado a la excepción.
     *
     * @return HttpStatus código 400.
     */
    public HttpStatus getStatus() {
        return status;
    }
}