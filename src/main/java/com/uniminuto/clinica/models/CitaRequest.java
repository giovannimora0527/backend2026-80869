package com.uniminuto.clinica.models;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CitaRequest {

    private Integer clienteId;
    private Integer mascotaId;
    private Integer medicoId;
    private LocalDateTime fechaHora;
    private String estado;
    private String motivo;
}