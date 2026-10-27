package com.uniminuto.clinica.models;

import lombok.Data;

@Data
public class UsuarioRS {

    private Long id;
    private String username;
    private String email;
    private Integer rol;
    private String perfil;
}
