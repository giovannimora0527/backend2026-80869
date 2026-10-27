package com.uniminuto.clinica.models;

import lombok.Data;

/**
 * Respuesta del paso 1 (login): el usuario existe, la clave es correcta
 * y se envió el código OTP al correo.
 */
@Data
public class AuthRS {

    private Long usuarioId;
    private boolean login;
    private String username;
    private String emailEnmascarado;
}
