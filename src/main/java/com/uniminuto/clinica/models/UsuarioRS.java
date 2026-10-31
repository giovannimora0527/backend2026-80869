package com.uniminuto.clinica.models;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UsuarioRS {

    private Long id;
    private String username;
    private String email;
    private Integer rol;
    private String perfil;
    private Boolean activo;
    private LocalDateTime fechaCreacion;
}
