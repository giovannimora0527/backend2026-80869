package com.uniminuto.clinica.models;

import lombok.Data;

import java.time.LocalDate;

@Data
public class FormulaMedicaRq {

    private Long id;
    private Long citaId;
    private Long medicamentoId;
    private String dosis;
    private String indicaciones;
    private LocalDate fechaEmision;
    private String frecuencia;
    private String duracionTratamiento;
}
