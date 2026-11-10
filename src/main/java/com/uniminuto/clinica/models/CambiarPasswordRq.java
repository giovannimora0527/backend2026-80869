package com.uniminuto.clinica.models;

import lombok.Data;

/**
 * Datos de entrada para el cambio de contrasena del usuario autenticado.
 *
 * Requiere la contrasena actual (para validar la identidad) y la nueva
 * contrasena que se desea establecer.
 */
@Data
public class CambiarPasswordRq {

    /** Identificador del usuario que cambia su contrasena. */
    private Long usuarioId;

    /** Contrasena actual del usuario. */
    private String passwordActual;

    /** Nueva contrasena que se desea establecer. */
    private String passwordNueva;
}
