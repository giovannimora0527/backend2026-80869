package com.uniminuto.clinica.models;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Datos de salida de un medicamento del inventario, con la existencia total
 * calculada a partir de sus lotes y los indicadores de estado (agotado, stock
 * bajo, proximo a vencer, vencido).
 */
@Data
public class MedicamentoRs {

    private Long id;
    private String nombre;
    private String codigo;
    private String descripcion;
    private String presentacion;
    private Boolean requiereInvima;
    private String registroInvima;
    private BigDecimal precioCompra;
    private BigDecimal precioVenta;
    private Integer stockMinimo;
    private LocalDate fechaProximoPedido;
    private LocalDate fechaCompra;
    private LocalDate fechaVence;

    /** Existencia total: suma de las cantidades de todos los lotes. */
    private Integer existenciaTotal;

    /** Verdadero si la existencia total es cero. */
    private Boolean agotado;

    /** Verdadero si la existencia total es mayor que cero pero menor o igual al stock minimo. */
    private Boolean stockBajo;

    /** Verdadero si tiene al menos un lote proximo a vencer. */
    private Boolean tieneProximosAVencer;

    /** Verdadero si tiene al menos un lote vencido. */
    private Boolean tieneVencidos;

    /** Lotes del medicamento. */
    private List<LoteRs> lotes;
}
