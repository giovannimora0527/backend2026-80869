package com.uniminuto.clinica.models;

import lombok.Data;

/**
 * Resultado de la validacion de una fila del archivo de carga masiva.
 *
 * Permite identificar el numero de fila, los datos leidos, si la fila es valida
 * y, en caso de error, el motivo del rechazo.
 */
@Data
public class FilaCargaRs {

    /** Numero de fila en el archivo (iniciando en 1 para la primera fila de datos). */
    private Integer fila;

    private String nombre;
    private String codigo;
    private String presentacion;
    private Boolean requiereInvima;
    private String registroInvima;
    private String precioCompra;
    private String precioVenta;
    private String stockMinimo;
    private String numeroLote;
    private String cantidad;
    private String fechaVencimiento;

    /** Indica si la fila paso todas las validaciones. */
    private Boolean valida;

    /** Motivo del rechazo cuando la fila no es valida. */
    private String motivoError;
}
