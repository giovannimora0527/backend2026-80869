package com.uniminuto.clinica.models;

import com.uniminuto.clinica.entity.AnotacionHistoria;
import com.uniminuto.clinica.util.TextoUtil;

import java.time.LocalDateTime;

/**
 * Vista resumida de una anotación para el histórico (DataTable). La observación y el
 * resto del detalle se consultan con {@code GET /anotacion-historia/consultar/{id}}.
 */
public class AnotacionResumenRs {

    /** Identificador de la anotación. */
    private Long id;

    /** Identificador de la historia clínica a la que pertenece. */
    private Long historiaId;

    /** Fecha y hora de la anotación. */
    private LocalDateTime fecha;

    /** Nombre completo del veterinario. */
    private String medicoNombre;

    /** Descripción de lo realizado u observado. */
    private String descripcion;

    /** Diagnóstico o evolución. */
    private String diagnostico;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getHistoriaId() { return historiaId; }
    public void setHistoriaId(Long historiaId) { this.historiaId = historiaId; }
    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
    public String getMedicoNombre() { return medicoNombre; }
    public void setMedicoNombre(String medicoNombre) { this.medicoNombre = medicoNombre; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public String getDiagnostico() { return diagnostico; }
    public void setDiagnostico(String diagnostico) { this.diagnostico = diagnostico; }

    /**
     * Construye el resumen a partir de la entidad.
     *
     * @param anotacion anotación a resumir
     * @return resumen de la anotación
     */
    public static AnotacionResumenRs desde(AnotacionHistoria anotacion) {
        AnotacionResumenRs rs = new AnotacionResumenRs();
        rs.setId(anotacion.getId());
        rs.setHistoriaId(anotacion.getHistoria().getId());
        rs.setFecha(anotacion.getFecha());
        rs.setMedicoNombre(TextoUtil.nombreCompleto(anotacion.getMedico().getNombres(), anotacion.getMedico().getApellidos()));
        rs.setDescripcion(anotacion.getDescripcion());
        rs.setDiagnostico(anotacion.getDiagnostico());
        return rs;
    }
}
