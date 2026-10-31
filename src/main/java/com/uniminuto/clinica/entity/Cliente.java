package com.uniminuto.clinica.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDate;

/**
 * Cliente (dueño de una o varias mascotas) de la clínica veterinaria.
 * Un cliente nunca se borra físicamente: se desactiva con el campo {@code activo}.
 */
@Entity
@Table(name = "cliente")
@Data
public class Cliente {

    /** Identificador único del cliente. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cliente_id")
    private Long id;

    /** Usuario del sistema asociado (opcional). */
    @Column(name = "usuario_id", unique = true)
    private Integer usuarioId;

    /** Tipo de documento: CC, CE, TI o PA. */
    @Column(name = "tipo_documento", nullable = false, length = 10)
    private String tipoDocumento;

    /** Número de documento; no puede repetirse entre clientes. */
    @Column(name = "numero_documento", nullable = false, unique = true, length = 20)
    private String numeroDocumento;

    /** Nombres del cliente. */
    @Column(name = "nombres", nullable = false, length = 100)
    private String nombres;

    /** Apellidos del cliente. */
    @Column(name = "apellidos", nullable = false, length = 100)
    private String apellidos;

    /** Fecha de nacimiento. */
    @Column(name = "fecha_nacimiento", nullable = false)
    private LocalDate fechaNacimiento;

    /** Género (M, F u O); es opcional. */
    @Column(name = "genero", length = 1)
    private String genero;

    /** Teléfono de contacto; es opcional. */
    @Column(name = "telefono", length = 20)
    private String telefono;

    /** Dirección de residencia; es opcional. */
    @Column(name = "direccion", columnDefinition = "TEXT")
    private String direccion;

    /** Indica si el cliente está activo (borrado lógico). */
    @Column(name = "activo", nullable = false)
    private Boolean activo;
}
