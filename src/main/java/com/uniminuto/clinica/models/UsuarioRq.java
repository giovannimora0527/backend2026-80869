package com.uniminuto.clinica.models;

import lombok.Data;

/**
 * Datos para crear o editar un usuario del sistema.
 * La contraseña es obligatoria al crear y opcional al editar
 * (si viene vacía se conserva la actual).
 */
@Data
public class UsuarioRq {

    private String username;

    private String password;

    private String email;

    /** Id del perfil (1=Administrador, 2=Recepcion, 3=Medico). */
    private Integer rol;
}
