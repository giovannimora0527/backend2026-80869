package com.uniminuto.clinica.models;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/** Filtros del histórico de anotaciones de una historia clínica (todos opcionales). */
@Getter
@Setter
public class AnotacionFiltroRq extends PaginacionRq {

    /** Veterinario que registró la anotación. */
    private Long medicoId;

    /** Texto libre: busca en descripción, observación y diagnóstico/evolución. */
    private String texto;

    /** Fecha inicial (inclusive), formato yyyy-MM-dd. */
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fechaDesde;

    /** Fecha final (inclusive), formato yyyy-MM-dd. */
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fechaHasta;
}
