package com.uniminuto.clinica.models.filtro;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * Filtros del histórico de anotaciones. Todos son opcionales y se combinan con AND.
 */
public class AnotacionFiltro extends PaginacionFiltro {

    /** Texto libre: busca en descripción, observación, diagnóstico y veterinario. */
    private String texto;

    /** Identificador de la historia clínica. */
    private Long historiaId;

    /** Identificador de la mascota (todas las anotaciones de sus historias). */
    private Integer mascotaId;

    /** Identificador del veterinario que registró la anotación. */
    private Long medicoId;

    /** Fecha mínima de la anotación, incluida (yyyy-MM-dd). */
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fechaDesde;

    /** Fecha máxima de la anotación, incluida (yyyy-MM-dd). */
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fechaHasta;

    public String getTexto() { return texto; }
    public void setTexto(String texto) { this.texto = texto; }
    public Long getHistoriaId() { return historiaId; }
    public void setHistoriaId(Long historiaId) { this.historiaId = historiaId; }
    public Integer getMascotaId() { return mascotaId; }
    public void setMascotaId(Integer mascotaId) { this.mascotaId = mascotaId; }
    public Long getMedicoId() { return medicoId; }
    public void setMedicoId(Long medicoId) { this.medicoId = medicoId; }
    public LocalDate getFechaDesde() { return fechaDesde; }
    public void setFechaDesde(LocalDate fechaDesde) { this.fechaDesde = fechaDesde; }
    public LocalDate getFechaHasta() { return fechaHasta; }
    public void setFechaHasta(LocalDate fechaHasta) { this.fechaHasta = fechaHasta; }
}
