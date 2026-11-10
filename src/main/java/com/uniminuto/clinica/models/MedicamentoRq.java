package com.uniminuto.clinica.models;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Datos de entrada para crear o actualizar un medicamento del inventario.
 */
@Data
public class MedicamentoRq {

    private Long id;
    private String nombre;
    private String codigo;
    private String descripcion;
    private String presentacion;

    /** Indica si el producto requiere registro Invima. */
    private Boolean requiereInvima;

    /** Registro Invima (obligatorio cuando requiereInvima es verdadero). */
    private String registroInvima;

    private BigDecimal precioCompra;
    private BigDecimal precioVenta;
    private Integer stockMinimo;
    private LocalDate fechaProximoPedido;
    private LocalDate fechaCompra;
    private LocalDate fechaVence;

    /**
     * Lotes del medicamento. Opcional al crear o actualizar el catalogo; la
     * gestion de lotes tambien puede realizarse de forma independiente.
     */
    private List<LoteRq> lotes;
}
