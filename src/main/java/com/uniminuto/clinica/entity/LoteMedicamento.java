package com.uniminuto.clinica.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Lote de un medicamento.
 *
 * Permite que un mismo medicamento tenga varios lotes, cada uno con su propia
 * cantidad, precio de compra y fecha de vencimiento. La existencia actual de un
 * medicamento es la suma de las cantidades de sus lotes vigentes.
 *
 * En el modulo de despacho (Fase 5), la dispensacion descuenta de los lotes
 * priorizando los de vencimiento mas proximo (politica FEFO).
 */
@Entity
@Table(name = "lote_medicamento")
@Data
public class LoteMedicamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medicamento_id", nullable = false)
    private Medicamento medicamento;

    @Column(name = "numero_lote", nullable = false, length = 50)
    private String numeroLote;

    @Column(name = "cantidad", nullable = false)
    private Integer cantidad = 0;

    @Column(name = "precio_compra", nullable = false, precision = 12, scale = 2)
    private BigDecimal precioCompra = BigDecimal.ZERO;

    @Column(name = "fecha_ingreso", nullable = false)
    private LocalDate fechaIngreso;

    @Column(name = "fecha_vencimiento", nullable = false)
    private LocalDate fechaVencimiento;

    @Column(name = "fecha_creacion_registro", nullable = false)
    private LocalDateTime fechaCreacionRegistro;

    @Column(name = "fecha_modificacion_registro")
    private LocalDateTime fechaModificacionRegistro;
}
