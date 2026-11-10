package com.uniminuto.clinica.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entidad de catalogo de un medicamento o producto del inventario.
 *
 * Contiene la informacion unica del producto (nombre, codigo, registro Invima,
 * precios de referencia y stock minimo). La existencia real y las fechas de
 * vencimiento se gestionan por lotes en la entidad LoteMedicamento, dado que un
 * mismo medicamento puede tener varios lotes con distinto precio y vencimiento.
 */
@Entity
@Table(name = "medicamento")
@Data
public class Medicamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "nombre", nullable = false, length = 100, unique = true)
    private String nombre;

    /** Codigo de identificacion de 8 caracteres alfanumericos. */
    @Column(name = "codigo", length = 8, unique = true)
    private String codigo;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "presentacion", length = 100)
    private String presentacion;

    /**
     * Indica si el producto requiere registro Invima. Verdadero para
     * medicamentos (obligatorio); falso para utensilios u otros productos.
     */
    @Column(name = "requiere_invima", nullable = false)
    private Boolean requiereInvima = Boolean.TRUE;

    /** Registro Invima. Obligatorio solo cuando requiereInvima es verdadero. */
    @Column(name = "registro_invima", length = 50)
    private String registroInvima;

    /** Precio de compra de referencia. */
    @Column(name = "precio_compra", nullable = false, precision = 12, scale = 2)
    private BigDecimal precioCompra = BigDecimal.ZERO;

    /** Precio de venta al publico. */
    @Column(name = "precio_venta", nullable = false, precision = 12, scale = 2)
    private BigDecimal precioVenta = BigDecimal.ZERO;

    /** Stock minimo permitido. Por debajo de este valor se considera stock bajo. */
    @Column(name = "stock_minimo", nullable = false)
    private Integer stockMinimo = 0;

    /** Fecha estimada del proximo pedido de reabastecimiento. */
    @Column(name = "fecha_proximo_pedido")
    private LocalDate fechaProximoPedido;

    @Column(name = "fecha_compra", nullable = false)
    private LocalDate fechaCompra;

    @Column(name = "fecha_vence", nullable = false)
    private LocalDate fechaVence;

    @Column(name = "fecha_creacion_registro", nullable = false)
    private LocalDateTime fechaCreacionRegistro;

    @Column(name = "fecha_modificacion_registro")
    private LocalDateTime fechaModificacionRegistro;
}
