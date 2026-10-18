package com.uniminuto.clinica.models;

import lombok.Getter;
import lombok.Setter;

/**
 * Parámetros comunes de paginación y ordenamiento de todos los listados.
 * Se reciben como parámetros de consulta (query string): {@code ?page=0&size=10&sortBy=fechaHora&sortDir=desc}.
 */
@Getter
@Setter
public class PaginacionRq {

    /** Número de página, base 0. */
    private int page = 0;

    /** Cantidad de registros por página (máximo 100). */
    private int size = 10;

    /** Nombre público de la columna por la que se ordena (ver endpoints en el documento técnico). */
    private String sortBy;

    /** Dirección del orden: "asc" o "desc". */
    private String sortDir;
}
