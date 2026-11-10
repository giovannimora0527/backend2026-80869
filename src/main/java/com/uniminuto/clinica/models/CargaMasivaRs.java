package com.uniminuto.clinica.models;

import lombok.Data;

import java.util.List;

/**
 * Resultado de una operacion de carga masiva.
 *
 * Se utiliza tanto para la vista previa (validacion sin persistir) como para la
 * confirmacion (persistencia de las filas validas). Incluye los totales y el
 * detalle por fila, de modo que el frontend pueda mostrar la vista previa y el
 * informe de errores con el numero de fila y el motivo del rechazo.
 */
@Data
public class CargaMasivaRs {

    private Integer totalRegistros;
    private Integer registrosValidos;
    private Integer registrosConError;

    /** Indica si la operacion correspondio a una confirmacion (persistencia). */
    private Boolean confirmado;

    /** Mensaje general del resultado. */
    private String mensaje;

    /** Detalle por fila (datos leidos, validez y motivo de error). */
    private List<FilaCargaRs> filas;
}
