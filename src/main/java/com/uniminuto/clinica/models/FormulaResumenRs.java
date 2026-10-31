package com.uniminuto.clinica.models;

import com.uniminuto.clinica.entity.FormulaMedica;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Fila resumida de una fórmula para el DataTable. No incluye dosis completa,
 * indicaciones ni datos de la cita: eso es el detalle (GET /formula-medica/{id}).
 */
@Data
public class FormulaResumenRs {

    private Long id;
    private LocalDateTime fechaEmision;
    private LocalDateTime fechaCreacionRegistro;
    private Integer mascotaId;
    private String mascotaNombre;
    private String clienteNombre;
    private Long medicoId;
    private String medicoNombre;
    private String medicamentoNombre;
    private Boolean activo;

    /**
     * Construye la fila resumida a partir de la entidad.
     *
     * @param f fórmula a resumir.
     * @return la fila con los datos que muestra el listado.
     */
    public static FormulaResumenRs de(FormulaMedica f) {
        FormulaResumenRs rs = new FormulaResumenRs();
        rs.setId(f.getId());
        rs.setFechaEmision(f.getFechaEmision());
        rs.setFechaCreacionRegistro(f.getFechaCreacionRegistro());
        if (f.getMascota() != null) {
            rs.setMascotaId(f.getMascota().getMascotaId());
            rs.setMascotaNombre(f.getMascota().getNombreMascota());
            if (f.getMascota().getCliente() != null) {
                rs.setClienteNombre(f.getMascota().getCliente().getNombres()
                        + " " + f.getMascota().getCliente().getApellidos());
            }
        }
        if (f.getMedico() != null) {
            rs.setMedicoId(f.getMedico().getId());
            rs.setMedicoNombre(f.getMedico().getNombres() + " " + f.getMedico().getApellidos());
        }
        if (f.getMedicamento() != null) {
            rs.setMedicamentoNombre(f.getMedicamento().getNombre());
        }
        rs.setActivo(f.getActivo());
        return rs;
    }
}
