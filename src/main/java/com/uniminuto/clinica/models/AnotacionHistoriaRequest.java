package com.uniminuto.clinica.models;

import lombok.Data;

@Data
public class AnotacionHistoriaRequest {
    private Integer mascotaId;
    private Integer medicoId;
    private String descripcion;
}
