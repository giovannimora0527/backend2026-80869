package com.uniminuto.clinica.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Medicamento del inventario que puede incluirse en una fórmula médica.
 */
@Entity
@Table(name = "medicamento")
public class Medicamento {

    /** Identificador del medicamento. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    /** Nombre comercial o genérico; es único. */
    @Column(name = "nombre", nullable = false, unique = true, length = 100)
    private String nombre;

    /** Descripción del uso del medicamento. */
    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    /** Presentación (tabletas, solución, inyectable...). */
    @Column(name = "presentacion", length = 100)
    private String presentacion;

    /** Fecha de compra del lote. */
    @Column(name = "fecha_compra", nullable = false)
    private LocalDate fechaCompra;

    /** Fecha de vencimiento del lote. */
    @Column(name = "fecha_vence", nullable = false)
    private LocalDate fechaVence;

    /** Fecha y hora de creación del registro. */
    @Column(name = "fecha_creacion_registro", nullable = false)
    private LocalDateTime fechaCreacionRegistro;

    /** Fecha y hora de la última modificación del registro. */
    @Column(name = "fecha_modificacion_registro")
    private LocalDateTime fechaModificacionRegistro;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public String getPresentacion() { return presentacion; }
    public void setPresentacion(String presentacion) { this.presentacion = presentacion; }
    public LocalDate getFechaCompra() { return fechaCompra; }
    public void setFechaCompra(LocalDate fechaCompra) { this.fechaCompra = fechaCompra; }
    public LocalDate getFechaVence() { return fechaVence; }
    public void setFechaVence(LocalDate fechaVence) { this.fechaVence = fechaVence; }
    public LocalDateTime getFechaCreacionRegistro() { return fechaCreacionRegistro; }
    public void setFechaCreacionRegistro(LocalDateTime fechaCreacionRegistro) { this.fechaCreacionRegistro = fechaCreacionRegistro; }
    public LocalDateTime getFechaModificacionRegistro() { return fechaModificacionRegistro; }
    public void setFechaModificacionRegistro(LocalDateTime fechaModificacionRegistro) { this.fechaModificacionRegistro = fechaModificacionRegistro; }
}
