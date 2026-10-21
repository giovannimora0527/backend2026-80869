package com.uniminuto.clinica.models.filtro;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * Filtros del listado de historias clínicas. Todos son opcionales y se combinan con AND.
 */
public class HistoriaFiltro extends PaginacionFiltro {

    /** Texto libre: busca en mascota, cliente, veterinario, motivo y diagnóstico. */
    private String texto;

    /** Estado de la historia (Abierta o Cerrada). */
    private String estado;

    /** Identificador del cliente. */
    private Long clienteId;

    /** Identificador de la mascota. */
    private Integer mascotaId;

    /** Identificador del veterinario. */
    private Long medicoId;

    /** Identificador de la cita que originó la historia. */
    private Long citaId;

    /** Fecha mínima de la consulta, incluida (yyyy-MM-dd). */
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fechaDesde;

    /** Fecha máxima de la consulta, incluida (yyyy-MM-dd). */
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fechaHasta;

    public String getTexto() { return texto; }
    public void setTexto(String texto) { this.texto = texto; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public Long getClienteId() { return clienteId; }
    public void setClienteId(Long clienteId) { this.clienteId = clienteId; }
    public Integer getMascotaId() { return mascotaId; }
    public void setMascotaId(Integer mascotaId) { this.mascotaId = mascotaId; }
    public Long getMedicoId() { return medicoId; }
    public void setMedicoId(Long medicoId) { this.medicoId = medicoId; }
    public Long getCitaId() { return citaId; }
    public void setCitaId(Long citaId) { this.citaId = citaId; }
    public LocalDate getFechaDesde() { return fechaDesde; }
    public void setFechaDesde(LocalDate fechaDesde) { this.fechaDesde = fechaDesde; }
    public LocalDate getFechaHasta() { return fechaHasta; }
    public void setFechaHasta(LocalDate fechaHasta) { this.fechaHasta = fechaHasta; }
}
