package com.uniminuto.clinica.models;

import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Respuesta estándar de los listados paginados.
 *
 * @param <T> tipo de cada fila.
 */
@Data
public class PageRs<T> {

    /** Filas de la página actual. */
    private List<T> content;

    /** Número de página actual (base 0). */
    private int page;

    /** Tamaño de página solicitado. */
    private int size;

    /** Total de filas que cumplen los filtros (todas las páginas). */
    private long totalElements;

    /** Total de páginas. */
    private int totalPages;

    /**
     * Convierte una página de Spring Data en la respuesta de la API.
     *
     * @param pagina página ya transformada al tipo de respuesta.
     * @param <T>    tipo de cada fila.
     * @return la respuesta lista para enviar.
     */
    public static <T> PageRs<T> de(Page<T> pagina) {
        PageRs<T> rs = new PageRs<>();
        rs.setContent(pagina.getContent());
        rs.setPage(pagina.getNumber());
        rs.setSize(pagina.getSize());
        rs.setTotalElements(pagina.getTotalElements());
        rs.setTotalPages(pagina.getTotalPages());
        return rs;
    }
}
