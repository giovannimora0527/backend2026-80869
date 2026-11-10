package com.uniminuto.clinica.models;

import lombok.Data;

/**
 * Objeto de peticion (request) para validar un token OTP.
 * Contiene los datos del usuario y el token que se desea validar.
 */
@Data
public class ValidateTokenRq {

    /** Identificador del usuario dueno del token. */
    private Long usuarioId;

    /** Codigo OTP (token) a validar. */
    private String token;

    /** Nombre de usuario (para verificar que coincide con el usuarioId). */
    private String username;
}
