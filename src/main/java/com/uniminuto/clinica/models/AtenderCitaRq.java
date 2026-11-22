package com.uniminuto.clinica.models;

import lombok.Data;

import java.util.List;

/**
 * Datos de entrada para atender una cita en un solo paso.
 *
 * Reune la informacion que el medico registra al atender la cita desde el
 * calendario: la anotacion de la atencion (descripcion, diagnostico y
 * observaciones) y, de forma opcional, la formula medica con uno o varios
 * medicamentos. El medico que atiende es siempre el asignado a la cita, por lo
 * que no se recibe aqui; se toma de la propia cita en el servidor.
 */
@Data
public class AtenderCitaRq {

    /** Identificador de la cita que se va a atender. */
    private Long citaId;

    /** Descripcion de la atencion (obligatoria). */
    private String descripcion;

    /** Diagnostico de la atencion (obligatorio). */
    private String diagnostico;

    /** Observaciones de la atencion (obligatorias). */
    private String observaciones;

    /** Indicaciones generales de la formula (opcional). */
    private String indicaciones;

    /**
     * Medicamentos a formular (opcional). Si la lista viene vacia o nula, la
     * atencion no genera formula medica; solo se registra la anotacion.
     */
    private List<FormulaDetalleRq> medicamentos;
}
