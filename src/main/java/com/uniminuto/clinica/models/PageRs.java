package com.uniminuto.clinica.models;

import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Respuesta paginada estándar para los listados.
 *
 * @param <T> Tipo de los elementos de la página.
 */
@Data
public class PageRs<T> {

    /** Elementos de la página actual. */
    private List<T> content;

    /** Número de la página actual, base 0. */
    private int page;

    /** Tamaño de página solicitado. */
    private int size;

    /** Total de registros que cumplen los filtros. */
    private long totalElements;

    /** Total de páginas disponibles. */
    private int totalPages;

    /** Indica si es la primera página. */
    private boolean first;

    /** Indica si es la última página. */
    private boolean last;

    /**
     * Convierte una página de Spring Data en la respuesta estándar de la API.
     *
     * @param p   Página de Spring Data.
     * @param <T> Tipo de los elementos.
     * @return Respuesta paginada.
     */
    public static <T> PageRs<T> de(Page<T> p) {
        PageRs<T> rs = new PageRs<>();
        rs.setContent(p.getContent());
        rs.setPage(p.getNumber());
        rs.setSize(p.getSize());
        rs.setTotalElements(p.getTotalElements());
        rs.setTotalPages(p.getTotalPages());
        rs.setFirst(p.isFirst());
        rs.setLast(p.isLast());
        return rs;
    }
}
