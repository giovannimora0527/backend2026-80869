package com.uniminuto.clinica.models;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Datos de salida de una formula medica con varios medicamentos, incluyendo la
 * informacion de la cita, la mascota, el propietario y el medico, para mostrar
 * en pantalla y generar el PDF.
 */
@Data
public class FormulaNuevaRs {

    private Long id;
    private Long citaId;
    private LocalDateTime fechaCreacion;
    private String indicaciones;

    private String mascotaNombre;
    private String clienteNombre;
    private String medicoNombre;

    private List<FormulaDetalleRs> medicamentos;
}
