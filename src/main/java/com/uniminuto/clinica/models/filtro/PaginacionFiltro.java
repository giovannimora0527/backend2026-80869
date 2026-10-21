package com.uniminuto.clinica.models.filtro;

/**
 * Parámetros comunes de paginación y ordenamiento de los listados. Se enlazan
 * automáticamente desde la query string, por ejemplo
 * {@code ?page=0&size=10&sortBy=fechaHora&sortDir=desc}.
 */
public class PaginacionFiltro {

    /** Número de página solicitada, iniciando en 0. */
    private Integer page = 0;

    /** Cantidad de registros por página (entre 1 y 100). */
    private Integer size = 10;

    /** Clave de la columna por la que se ordena; si es nula se usa el orden por defecto. */
    private String sortBy;

    /** Dirección del orden: {@code asc} o {@code desc}; si es nula se usa la por defecto. */
    private String sortDir;

    public Integer getPage() { return page; }
    public void setPage(Integer page) { this.page = page; }
    public Integer getSize() { return size; }
    public void setSize(Integer size) { this.size = size; }
    public String getSortBy() { return sortBy; }
    public void setSortBy(String sortBy) { this.sortBy = sortBy; }
    public String getSortDir() { return sortDir; }
    public void setSortDir(String sortDir) { this.sortDir = sortDir; }
}
