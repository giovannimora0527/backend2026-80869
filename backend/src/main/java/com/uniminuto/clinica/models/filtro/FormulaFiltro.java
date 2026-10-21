package com.uniminuto.clinica.models.filtro;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * Filtros del listado de fórmulas médicas. Todos son opcionales y se combinan con AND.
 */
public class FormulaFiltro extends PaginacionFiltro {

    /** Texto libre: busca en mascota, cliente, veterinario, medicamento, dosis y frecuencia. */
    private String texto;

    /** {@code true} solo activas, {@code false} solo inactivas, nulo todas. */
    private Boolean activo;

    /** Identificador del cliente propietario de la mascota. */
    private Long clienteId;

    /** Identificador de la mascota. */
    private Integer mascotaId;

    /** Identificador del veterinario que emitió la fórmula. */
    private Long medicoId;

    /** Identificador del medicamento. */
    private Long medicamentoId;

    /** Identificador de la cita (consulta). */
    private Long citaId;

    /** Fecha mínima de creación, incluida (yyyy-MM-dd). */
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fechaDesde;

    /** Fecha máxima de creación, incluida (yyyy-MM-dd). */
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fechaHasta;

    public String getTexto() { return texto; }
    public void setTexto(String texto) { this.texto = texto; }
    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
    public Long getClienteId() { return clienteId; }
    public void setClienteId(Long clienteId) { this.clienteId = clienteId; }
    public Integer getMascotaId() { return mascotaId; }
    public void setMascotaId(Integer mascotaId) { this.mascotaId = mascotaId; }
    public Long getMedicoId() { return medicoId; }
    public void setMedicoId(Long medicoId) { this.medicoId = medicoId; }
    public Long getMedicamentoId() { return medicamentoId; }
    public void setMedicamentoId(Long medicamentoId) { this.medicamentoId = medicamentoId; }
    public Long getCitaId() { return citaId; }
    public void setCitaId(Long citaId) { this.citaId = citaId; }
    public LocalDate getFechaDesde() { return fechaDesde; }
    public void setFechaDesde(LocalDate fechaDesde) { this.fechaDesde = fechaDesde; }
    public LocalDate getFechaHasta() { return fechaHasta; }
    public void setFechaHasta(LocalDate fechaHasta) { this.fechaHasta = fechaHasta; }
}
