package com.uniminuto.clinica.models;

import lombok.Data;

import java.util.List;

/**
 * Datos de salida de un rol (perfil), incluyendo los identificadores de los
 * menus que tiene asignados.
 */
@Data
public class RolRs {

    private Integer perfilId;
    private String nombre;
    private Boolean activo;

    /** Identificadores de los menus asignados al rol. */
    private List<Integer> menuIds;
}
