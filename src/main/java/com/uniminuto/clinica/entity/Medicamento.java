package com.uniminuto.clinica.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "medicamento")
@Data
public class Medicamento {

    public enum TipoProducto {
        MEDICAMENTO,
        UTENSILIO
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "nombre", nullable = false, length = 100, unique = true)
    private String nombre;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "presentacion", length = 100)
    private String presentacion;

    @Column(name = "fecha_compra")
    private LocalDate fechaCompra;

    @Column(name = "fecha_vence")
    private LocalDate fechaVence;

    @Column(name = "fecha_creacion_registro", nullable = false)
    private LocalDateTime fechaCreacionRegistro;

    @Column(name = "fecha_modificacion_registro")
    private LocalDateTime fechaModificacionRegistro;

    @Column(name = "codigo", length = 8, unique = true)
    private String codigo;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_producto", nullable = false)
    private TipoProducto tipoProducto = TipoProducto.MEDICAMENTO;

    @Column(name = "registro_invima", length = 100)
    private String registroInvima;

    @Column(name = "precio_venta", precision = 12, scale = 2)
    private BigDecimal precioVenta;

    @Column(name = "stock_minimo", nullable = false)
    private Integer stockMinimo = 0;

    @Column(name = "fecha_proximo_pedido")
    private LocalDate fechaProximoPedido;
}