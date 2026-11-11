package com.uniminuto.clinica.models;

import lombok.Data;

/**
 * Informacion de un medicamento de la formula para el despacho: cantidad
 * prescrita y existencia disponible (no vencida) en inventario.
 */
@Data
public class MedicamentoDespachoRs {

    private Long medicamentoId;
    private String medicamentoNombre;
    private Integer cantidadPrescrita;
    private String dosis;
    private String frecuencia;
    private String duracion;

    /** Existencia total disponible (lotes no vencidos). */
    private Integer existenciaDisponible;

    /** Indica si hay existencia suficiente para la cantidad prescrita. */
    private Boolean suficiente;
}
