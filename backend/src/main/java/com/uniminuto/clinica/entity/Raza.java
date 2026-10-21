package com.uniminuto.clinica.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

/**
 * Raza de una mascota, asociada a una especie (por ejemplo Perro/Canino).
 */
@Entity
@Table(name = "raza")
public class Raza {

    /** Identificador de la raza. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "raza_id")
    private Integer razaId;

    /** Nombre de la raza. */
    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    /** Especie a la que pertenece. */
    @Column(name = "especie", nullable = false, length = 100)
    private String especie;

    /** Fecha y hora de creación del registro. */
    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    /** Fecha y hora de la última modificación. */
    @Column(name = "fecha_modificacion")
    private LocalDateTime fechaModificacion;

    public Integer getRazaId() { return razaId; }
    public void setRazaId(Integer razaId) { this.razaId = razaId; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getEspecie() { return especie; }
    public void setEspecie(String especie) { this.especie = especie; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
    public LocalDateTime getFechaModificacion() { return fechaModificacion; }
    public void setFechaModificacion(LocalDateTime fechaModificacion) { this.fechaModificacion = fechaModificacion; }
}
