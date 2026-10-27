package com.uniminuto.clinica.models;

import lombok.Data;

@Data
public class ValidarOtpRq {

    private Long usuarioId;
    private String codigo;
}
