package com.uniminuto.clinica.models;

import lombok.Data;

import java.util.List;

/**
 * Datos de entrada para crear o actualizar un rol (perfil) y los menus
 * (modulos) que tiene asignados.
 */
@Data
public class RolRq {

    private Integer perfilId;
    private String nombre;
    private Boolean activo;

    /** Identificadores de los menus que el rol podra ver. */
    private List<Integer> menuIds;
}
