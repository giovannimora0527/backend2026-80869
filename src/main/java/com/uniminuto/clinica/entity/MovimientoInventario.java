package com.uniminuto.clinica.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Movimiento de inventario (entrada o salida).
 *
 * Registra cada variacion de existencias con trazabilidad: tipo de movimiento,
 * cantidad, fecha, usuario responsable y una referencia al origen (por ejemplo
 * una carga masiva o, en fases posteriores, un despacho de formula medica).
 *
 * El tipo se maneja como texto con los valores ENTRADA y SALIDA.
 */
@Entity
@Table(name = "movimiento_inventario")
@Data
public class MovimientoInventario {

    public static final String TIPO_ENTRADA = "ENTRADA";
    public static final String TIPO_SALIDA = "SALIDA";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "medicamento_id", nullable = false)
    private Long medicamentoId;

    @Column(name = "lote_id")
    private Long loteId;

    @Column(name = "tipo", nullable = false, length = 10)
    private String tipo;

    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    @Column(name = "fecha", nullable = false)
    private LocalDateTime fecha;

    @Column(name = "usuario_responsable", length = 100)
    private String usuarioResponsable;

    @Column(name = "referencia", length = 150)
    private String referencia;

    @Column(name = "observacion", length = 255)
    private String observacion;
}
