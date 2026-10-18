package com.uniminuto.clinica.models;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/** Filtros del listado de citas (todos opcionales; se combinan con AND). */
@Getter
@Setter
public class CitaFiltroRq extends PaginacionRq {

    /** Cliente propietario de la mascota. */
    private Long clienteId;

    /** Mascota de la cita. */
    private Integer mascotaId;

    /** Veterinario asignado. */
    private Long medicoId;

    /** Estado de la cita (PENDIENTE, PROGRAMADA, CONFIRMADA, ATENDIDA, CANCELADA). */
    private String estado;

    /** Texto libre: busca en cliente, documento, mascota, veterinario y motivo. */
    private String texto;

    /** Fecha inicial del rango (inclusive), formato yyyy-MM-dd. */
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fechaDesde;

    /** Fecha final del rango (inclusive), formato yyyy-MM-dd. */
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fechaHasta;
}
