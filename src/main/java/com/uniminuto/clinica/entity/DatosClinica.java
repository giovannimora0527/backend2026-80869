package com.uniminuto.clinica.entity;

import jakarta.persistence.*;
import lombok.Data;

/**
 * Datos de la clinica que se imprimen en la cabecera de las facturas.
 * Se gestiona como un unico registro de configuracion.
 */
@Entity
@Table(name = "datos_clinica")
@Data
public class DatosClinica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "nombre", nullable = false, length = 150)
    private String nombre;

    @Column(name = "nit", length = 30)
    private String nit;

    @Column(name = "telefono", length = 30)
    private String telefono;

    @Column(name = "direccion", length = 200)
    private String direccion;

    @Column(name = "correo", length = 150)
    private String correo;
}
