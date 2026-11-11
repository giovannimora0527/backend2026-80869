package com.uniminuto.clinica.models;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Datos de salida de una factura, con la informacion del propietario, mascota y
 * medico, los totales, el estado de pago, el saldo pendiente y su detalle.
 */
@Data
public class FacturaRs {

    private Long id;
    private Long citaId;
    private LocalDateTime fecha;

    private String clienteNombre;
    private String mascotaNombre;
    private String medicoNombre;

    private BigDecimal subtotal;
    private BigDecimal iva;
    private BigDecimal descuento;
    private BigDecimal total;

    private String estadoPago;
    private BigDecimal totalPagado;
    private BigDecimal saldoPendiente;

    private String observacion;

    private List<FacturaDetalleRs> items;
}
