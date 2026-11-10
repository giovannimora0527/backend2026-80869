package com.uniminuto.clinica.models;

import lombok.Data;

/**
 * Datos de entrada para el proceso de recuperacion de contrasena.
 * El usuario proporciona unicamente su correo electronico registrado.
 */
@Data
public class RecuperarPasswordRq {

    /** Correo electronico del usuario que solicita la recuperacion. */
    private String email;
}
