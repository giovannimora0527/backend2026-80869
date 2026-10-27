package com.uniminuto.clinica.models;

import lombok.Data;

import java.util.List;

/**
 * Respuesta del paso 2 (validar OTP): token JWT, datos del usuario
 * y lista de menus a los que tiene acceso.
 */
@Data
public class LoginRS {

    private String token;
    private long expiraEnSegundos;
    private UsuarioRS usuario;
    private List<MenuRS> menuList;
}
