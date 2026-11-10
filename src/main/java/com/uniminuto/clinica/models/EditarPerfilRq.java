package com.uniminuto.clinica.models;

import lombok.Data;

/**
 * Datos de entrada para la edicion del perfil del usuario autenticado.
 *
 * Alcance (Opcion A): el usuario solo puede modificar su nombre de usuario y
 * su correo electronico. Los datos personales de contacto (nombres, telefono,
 * direccion) se gestionan en las entidades Cliente o Medico segun corresponda,
 * por lo que no se duplican en la tabla usuario.
 */
@Data
public class EditarPerfilRq {

    /** Identificador del usuario que edita su perfil. */
    private Long usuarioId;

    /** Nuevo nombre de usuario. */
    private String username;

    /** Nuevo correo electronico. */
    private String email;
}
