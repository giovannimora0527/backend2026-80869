package com.uniminuto.clinica.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Item de una factura. Puede ser un servicio o un medicamento. El precio
 * unitario y el IVA se guardan en el detalle para conservar historicamente el
 * valor cobrado, aunque luego cambie la tarifa.
 */
@Entity
@Table(name = "factura_detalle")
@Data
public class FacturaDetalle {

    public static final String TIPO_SERVICIO = "SERVICIO";
    public static final String TIPO_MEDICAMENTO = "MEDICAMENTO";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "factura_id", nullable = false)
    private Long facturaId;

    @Column(name = "tipo", nullable = false, length = 15)
    private String tipo;

    /** Id del servicio o medicamento referenciado. */
    @Column(name = "referencia_id")
    private Long referenciaId;

    @Column(name = "descripcion", nullable = false, length = 200)
    private String descripcion;

    @Column(name = "cantidad", nullable = false)
    private Integer cantidad = 1;

    @Column(name = "precio_unitario", nullable = false, precision = 14, scale = 2)
    private BigDecimal precioUnitario = BigDecimal.ZERO;

    @Column(name = "iva_porcentaje", nullable = false, precision = 5, scale = 2)
    private BigDecimal ivaPorcentaje = BigDecimal.ZERO;

    @Column(name = "subtotal", nullable = false, precision = 14, scale = 2)
    private BigDecimal subtotal = BigDecimal.ZERO;
}
