package com.uniminuto.clinica.models;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Datos de salida de un despacho realizado, para el historial y el comprobante.
 */
@Data
public class DespachoRs {

    private Long id;
    private Long formulaId;
    private String estado;
    private LocalDateTime fecha;
    private String usuarioResponsable;

    private String clienteNombre;
    private String mascotaNombre;
    private String medicoNombre;

    private List<DespachoDetalleRs> items;

    @Data
    public static class DespachoDetalleRs {
        private String medicamentoNombre;
        private Long loteId;
        private String numeroLote;
        private Integer cantidadSolicitada;
        private Integer cantidadEntregada;
    }
}
