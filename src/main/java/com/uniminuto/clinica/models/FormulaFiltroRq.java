package com.uniminuto.clinica.models;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/** Filtros del listado de fórmulas médicas (todos opcionales; se combinan con AND). */
@Getter
@Setter
public class FormulaFiltroRq extends PaginacionRq {

    /** Cliente propietario de la mascota. */
    private Long clienteId;

    /** Mascota a la que se formula. */
    private Integer mascotaId;

    /** Veterinario que emitió la fórmula. */
    private Long medicoId;

    /** Medicamento recetado. */
    private Long medicamentoId;

    /** Cita (consulta) asociada. */
    private Long citaId;

    /** Estado: {@code true} activas, {@code false} inactivas, nulo todas. */
    private Boolean activo;

    /** Texto libre: busca en mascota, cliente, veterinario y medicamento. */
    private String texto;

    /** Fecha de emisión inicial (inclusive), formato yyyy-MM-dd. */
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fechaDesde;

    /** Fecha de emisión final (inclusive), formato yyyy-MM-dd. */
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fechaHasta;
}
