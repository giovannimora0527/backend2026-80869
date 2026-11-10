package com.uniminuto.clinica.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Registro (log) de un proceso de carga masiva de inventario.
 *
 * Deja constancia de cada importacion: fecha, usuario responsable, archivo
 * procesado y los totales de registros (total, validos y con error). El campo
 * detalle almacena un resumen textual del resultado de la carga.
 */
@Entity
@Table(name = "log_carga_inventario")
@Data
public class LogCargaInventario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "fecha", nullable = false)
    private LocalDateTime fecha;

    @Column(name = "usuario_responsable", length = 100)
    private String usuarioResponsable;

    @Column(name = "nombre_archivo", length = 255)
    private String nombreArchivo;

    @Column(name = "total_registros", nullable = false)
    private Integer totalRegistros = 0;

    @Column(name = "registros_validos", nullable = false)
    private Integer registrosValidos = 0;

    @Column(name = "registros_con_error", nullable = false)
    private Integer registrosConError = 0;

    @Column(name = "detalle", columnDefinition = "TEXT")
    private String detalle;
}
