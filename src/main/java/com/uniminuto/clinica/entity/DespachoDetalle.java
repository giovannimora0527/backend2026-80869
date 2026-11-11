package com.uniminuto.clinica.entity;

import jakarta.persistence.*;
import lombok.Data;

/**
 * Detalle del despacho: por cada medicamento de la formula, la cantidad
 * solicitada, la cantidad efectivamente entregada y el lote del que se
 * descuenta.
 */
@Entity
@Table(name = "despacho_detalle")
@Data
public class DespachoDetalle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "despacho_id", nullable = false)
    private Long despachoId;

    @Column(name = "medicamento_id", nullable = false)
    private Long medicamentoId;

    @Column(name = "lote_id")
    private Long loteId;

    @Column(name = "cantidad_solicitada", nullable = false)
    private Integer cantidadSolicitada = 0;

    @Column(name = "cantidad_entregada", nullable = false)
    private Integer cantidadEntregada = 0;
}
