package com.uniminuto.clinica.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Cabecera del despacho (dispensacion) de una formula medica.
 *
 * Representa el proceso de entrega de los medicamentos de una formula. Su estado
 * refleja el avance del despacho. El descuento del inventario se realiza al
 * confirmar la entrega, de forma transaccional.
 */
@Entity
@Table(name = "despacho")
@Data
public class Despacho {

    public static final String ESTADO_PENDIENTE = "PENDIENTE";
    public static final String ESTADO_EN_PREPARACION = "EN_PREPARACION";
    public static final String ESTADO_PARCIAL = "DESPACHO_PARCIAL";
    public static final String ESTADO_DESPACHADO = "DESPACHADO";
    public static final String ESTADO_PENDIENTE_DISPONIBILIDAD = "PENDIENTE_DISPONIBILIDAD";
    public static final String ESTADO_CANCELADO = "CANCELADO";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "formula_id", nullable = false)
    private Long formulaId;

    @Column(name = "estado", nullable = false, length = 30)
    private String estado = ESTADO_PENDIENTE;

    @Column(name = "fecha", nullable = false)
    private LocalDateTime fecha;

    @Column(name = "usuario_responsable", length = 100)
    private String usuarioResponsable;

    @Column(name = "observacion", length = 255)
    private String observacion;
}
