package com.uniminuto.clinica.models;

import lombok.Data;

/**
 * Datos de salida de un perfil (rol) del sistema.
 * Se utiliza para poblar la seleccion de rol en la gestion de usuarios.
 */
@Data
public class PerfilRs {

    /** Identificador del perfil. */
    private Integer perfilId;

    /** Nombre del perfil (por ejemplo: Administrador, Recepcion, Medico). */
    private String nombre;

    /** Indica si el perfil esta activo. */
    private Boolean activo;
}
