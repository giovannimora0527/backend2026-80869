package com.uniminuto.clinica.models;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * Parámetros de consulta (query string) que aceptan los listados paginados.
 * Todos son opcionales; cada servicio usa solo los que le aplican.
 */
@Data
public class FiltroPaginadoRq {

    /** Página (base 0). */
    private Integer page;

    /** Tamaño de página (1 a 100). */
    private Integer size;

    /** Columna por la que se ordena (nombre lógico, validado por el servicio). */
    private String sort;

    /** Dirección: {@code asc} o {@code desc}. */
    private String dir;

    /** Texto libre para buscar en varias columnas. */
    private String q;

    /** Estado de la cita (o de la consulta asociada). */
    private String estado;

    /** Fecha inicial del rango (AAAA-MM-DD), inclusive. */
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate desde;

    /** Fecha final del rango (AAAA-MM-DD), inclusive. */
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate hasta;

    /** Filtra por cliente. */
    private Long clienteId;

    /** Filtra por mascota. */
    private Integer mascotaId;

    /** Filtra por veterinario. */
    private Long medicoId;

    /** Filtra por medicamento. */
    private Long medicamentoId;

    /** Filtra por registros activos o inactivos (fórmulas). */
    private Boolean activo;
}
