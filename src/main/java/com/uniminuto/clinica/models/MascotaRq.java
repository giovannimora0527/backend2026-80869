package com.uniminuto.clinica.models;
import lombok.Data;

@Data
public class MascotaRq {

    private String nombre;
    private Integer edad;
    private Integer razaId;
    private Integer clienteId;
}
