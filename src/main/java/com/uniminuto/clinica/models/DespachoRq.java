package com.uniminuto.clinica.models;

import lombok.Data;

import java.util.List;

/**
 * Datos de entrada para confirmar el despacho de una formula. Por cada
 * medicamento se indica la cantidad que efectivamente se entrega.
 */
@Data
public class DespachoRq {

    private Long formulaId;
    private String observacion;
    private List<DespachoItemRq> items;

    @Data
    public static class DespachoItemRq {
        private Long medicamentoId;
        private Integer cantidadEntregada;
    }
}
