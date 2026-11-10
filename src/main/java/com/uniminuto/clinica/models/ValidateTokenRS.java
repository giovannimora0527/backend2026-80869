package com.uniminuto.clinica.models;

import lombok.Data;

/**
 * Objeto de respuesta (response) de la validacion de un token OTP.
 */
@Data
public class ValidateTokenRS {

    /** Token que se valido. */
    private String token;

    /** Indica si el logueo (validacion del token) fue exitoso. */
    private Boolean logueoExitoso;
}
