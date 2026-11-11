package com.uniminuto.clinica.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Cabecera de una factura. Conserva historicamente los totales (subtotal, IVA,
 * descuento y total) y el estado de pago. Se relaciona con la cita, el cliente,
 * la mascota y el medico por sus identificadores.
 */
@Entity
@Table(name = "factura")
@Data
public class Factura {

    public static final String ESTADO_PENDIENTE = "PENDIENTE";
    public static final String ESTADO_PAGO_PARCIAL = "PAGO_PARCIAL";
    public static final String ESTADO_PAGADA = "PAGADA";
    public static final String ESTADO_ANULADA = "ANULADA";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "cita_id")
    private Long citaId;

    @Column(name = "cliente_id")
    private Long clienteId;

    @Column(name = "mascota_id")
    private Long mascotaId;

    @Column(name = "medico_id")
    private Long medicoId;

    @Column(name = "fecha", nullable = false)
    private LocalDateTime fecha;

    @Column(name = "subtotal", nullable = false, precision = 14, scale = 2)
    private BigDecimal subtotal = BigDecimal.ZERO;

    @Column(name = "iva", nullable = false, precision = 14, scale = 2)
    private BigDecimal iva = BigDecimal.ZERO;

    @Column(name = "descuento", nullable = false, precision = 14, scale = 2)
    private BigDecimal descuento = BigDecimal.ZERO;

    @Column(name = "total", nullable = false, precision = 14, scale = 2)
    private BigDecimal total = BigDecimal.ZERO;

    @Column(name = "estado_pago", nullable = false, length = 20)
    private String estadoPago = ESTADO_PENDIENTE;

    @Column(name = "observacion", length = 255)
    private String observacion;
}
