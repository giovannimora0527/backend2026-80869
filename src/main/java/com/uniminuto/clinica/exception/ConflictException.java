package com.uniminuto.clinica.exception;

import org.springframework.http.HttpStatus;

/**
 * Excepción para indicar un conflicto con el estado actual de los datos (HTTP 409).
 * <p>
 * Ejemplos: documento duplicado, cita en el mismo horario, o intentar
 * desactivar un registro que todavía tiene información activa relacionada.
 */
public class ConflictException extends RuntimeException {

    /** Código HTTP asociado al error. */
    private final HttpStatus status;

    /**
     * Crea la excepción con un mensaje entendible para el usuario.
     *
     * @param message mensaje que verá el usuario (sin detalles técnicos).
     */
    public ConflictException(String message) {
        super(message);
        this.status = HttpStatus.CONFLICT;
    }

    /**
     * Obtiene el código HTTP del error.
     *
     * @return siempre {@link HttpStatus#CONFLICT}.
     */
    public HttpStatus getStatus() {
        return status;
    }
}
