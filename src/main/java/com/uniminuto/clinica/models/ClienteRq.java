package com.uniminuto.clinica.models;

import lombok.Data;

import java.time.LocalDate;

/**
 * Datos que envía el frontend para crear o editar un cliente.
 * El identificador no viaja aquí: en la edición va en la URL.
 */
@Data
public class ClienteRq {

    /** Tipo de documento: CC, CE, TI o PA. */
    private String tipoDocumento;

    /** Número de documento (único por cliente). */
    private String numeroDocumento;

    /** Nombres del cliente. */
    private String nombres;

    /** Apellidos del cliente. */
    private String apellidos;

    /** Fecha de nacimiento (formato AAAA-MM-DD). */
    private LocalDate fechaNacimiento;

    /** Género: M, F u O. Es opcional. */
    private String genero;

    /** Teléfono de contacto. Es opcional. */
    private String telefono;

    /** Dirección de residencia. Es opcional. */
    private String direccion;
}
