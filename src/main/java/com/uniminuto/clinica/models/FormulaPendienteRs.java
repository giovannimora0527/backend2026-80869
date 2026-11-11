package com.uniminuto.clinica.models;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Formula pendiente de despacho, con la informacion necesaria para que el
 * personal de despacho la identifique y prepare la entrega.
 */
@Data
public class FormulaPendienteRs {

    private Long formulaId;
    private LocalDateTime fechaGeneracion;
    private String medicoNombre;
    private String clienteNombre;
    private String mascotaNombre;
    private String estadoDespacho;

    /** Medicamentos de la formula con su disponibilidad en inventario. */
    private List<MedicamentoDespachoRs> medicamentos;
}
