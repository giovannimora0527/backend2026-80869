package com.uniminuto.clinica.models;

import lombok.Data;

/**
 * Datos de salida de un menu (modulo) del sistema. Se usa para listar los menus
 * disponibles al configurar un rol.
 */
@Data
public class MenuRs {

    private Integer menuId;
    private String code;
    private String title;
    private String url;
    private String icon;
    private Boolean activo;
}
