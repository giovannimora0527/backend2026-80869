package com.uniminuto.clinica.models;

import com.uniminuto.clinica.entity.FormulaMedica;
import com.uniminuto.clinica.util.TextoUtil;

import java.time.LocalDateTime;

/**
 * Vista resumida de una fórmula médica para el listado (DataTable). No incluye
 * indicaciones, duración ni identificadores de relaciones: eso es parte del detalle,
 * disponible en {@code GET /formula-medica/consultar/{id}}.
 */
public class FormulaResumenRs {

    /** Identificador de la fórmula. */
    private Long id;

    /** Fecha y hora de creación del registro. */
    private LocalDateTime fechaCreacion;

    /** Nombre de la mascota. */
    private String mascotaNombre;

    /** Nombre completo del cliente propietario. */
    private String clienteNombre;

    /** Nombre completo del veterinario que emitió la fórmula. */
    private String medicoNombre;

    /** Nombre del medicamento recetado. */
    private String medicamentoNombre;

    /** Dosis recetada. */
    private String dosis;

    /** Frecuencia de administración. */
    private String frecuencia;

    /** Indica si la fórmula está activa. */
    private Boolean activo;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
    public String getMascotaNombre() { return mascotaNombre; }
    public void setMascotaNombre(String mascotaNombre) { this.mascotaNombre = mascotaNombre; }
    public String getClienteNombre() { return clienteNombre; }
    public void setClienteNombre(String clienteNombre) { this.clienteNombre = clienteNombre; }
    public String getMedicoNombre() { return medicoNombre; }
    public void setMedicoNombre(String medicoNombre) { this.medicoNombre = medicoNombre; }
    public String getMedicamentoNombre() { return medicamentoNombre; }
    public void setMedicamentoNombre(String medicamentoNombre) { this.medicamentoNombre = medicamentoNombre; }
    public String getDosis() { return dosis; }
    public void setDosis(String dosis) { this.dosis = dosis; }
    public String getFrecuencia() { return frecuencia; }
    public void setFrecuencia(String frecuencia) { this.frecuencia = frecuencia; }
    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }

    /**
     * Construye el resumen a partir de la entidad.
     *
     * @param formula fórmula a resumir
     * @return resumen de la fórmula
     */
    public static FormulaResumenRs desde(FormulaMedica formula) {
        FormulaResumenRs rs = new FormulaResumenRs();
        rs.setId(formula.getId());
        rs.setFechaCreacion(formula.getFechaCreacionRegistro());
        rs.setMascotaNombre(formula.getMascota().getNombreMascota());
        rs.setClienteNombre(TextoUtil.nombreCompleto(formula.getMascota().getCliente().getNombres(),
                formula.getMascota().getCliente().getApellidos()));
        rs.setMedicoNombre(TextoUtil.nombreCompleto(formula.getMedico().getNombres(), formula.getMedico().getApellidos()));
        rs.setMedicamentoNombre(formula.getMedicamento().getNombre());
        rs.setDosis(formula.getDosis());
        rs.setFrecuencia(formula.getFrecuencia());
        rs.setActivo(formula.getActivo());
        return rs;
    }
}
