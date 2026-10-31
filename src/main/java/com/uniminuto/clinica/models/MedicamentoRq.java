package com.uniminuto.clinica.models;

import lombok.Data;

import java.time.LocalDate;

/**
 * Datos que envía el frontend para crear o editar un medicamento.
 * El identificador no viaja aquí: en la edición va en la URL.
 */
@Data
public class MedicamentoRq {

    /** Nombre del medicamento (único, sin importar mayúsculas). */
    private String nombre;

    /** Descripción o uso general. Es opcional. */
    private String descripcion;

    /** Presentación comercial (por ejemplo "Tabletas 250 mg"). Es opcional. */
    private String presentacion;

    /** Fecha en que se compró el lote (formato AAAA-MM-DD). */
    private LocalDate fechaCompra;

    /** Fecha de vencimiento del lote (formato AAAA-MM-DD). */
    private LocalDate fechaVence;
}
