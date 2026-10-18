package com.uniminuto.clinica.models;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/** Filtros del listado de historias clínicas (todos opcionales; se combinan con AND). */
@Getter
@Setter
public class HistoriaFiltroRq extends PaginacionRq {

    /** Cliente propietario de la mascota. */
    private Long clienteId;

    /** Mascota (paciente). */
    private Integer mascotaId;

    /** Veterinario tratante. */
    private Long medicoId;

    /** Estado de la historia (ACTIVA o CERRADA). */
    private String estado;

    /** Texto libre: busca en cliente, mascota, veterinario, motivo y diagnóstico. */
    private String texto;

    /** Fecha inicial de la consulta (inclusive), formato yyyy-MM-dd. */
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fechaDesde;

    /** Fecha final de la consulta (inclusive), formato yyyy-MM-dd. */
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fechaHasta;
}
