package com.uniminuto.clinica.exception;

import org.springframework.http.HttpStatus;

/**
 * Excepción para indicar que un recurso solicitado no existe (HTTP 404).
 * <p>
 * Se usa cuando el identificador recibido es válido en formato, pero no hay
 * ningún registro con ese valor en la base de datos.
 */
public class NotFoundException extends RuntimeException {

    /** Código HTTP asociado al error. */
    private final HttpStatus status;

    /**
     * Crea la excepción con un mensaje entendible para el usuario.
     *
     * @param message mensaje que verá el usuario (sin detalles técnicos).
     */
    public NotFoundException(String message) {
        super(message);
        this.status = HttpStatus.NOT_FOUND;
    }

    /**
     * Obtiene el código HTTP del error.
     *
     * @return siempre {@link HttpStatus#NOT_FOUND}.
     */
    public HttpStatus getStatus() {
        return status;
    }
}
