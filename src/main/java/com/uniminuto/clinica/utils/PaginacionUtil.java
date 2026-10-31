package com.uniminuto.clinica.utils;

import com.uniminuto.clinica.exception.BadRequestException;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Map;

/**
 * Utilidades compartidas por los listados paginados: construcción segura del
 * {@link Pageable} (con lista blanca de columnas ordenables) y filtros de texto.
 */
public final class PaginacionUtil {

    /** Tamaño de página por defecto. */
    public static final int TAMANIO_POR_DEFECTO = 10;

    /** Tamaño máximo de página (evita pedir toda la tabla de un solo golpe). */
    public static final int TAMANIO_MAXIMO = 100;

    private PaginacionUtil() {
        // Clase utilitaria: no se instancia.
    }

    /**
     * Construye el {@link Pageable} validando página, tamaño y columna de orden.
     *
     * @param page         número de página (base 0); si es nulo, 0.
     * @param size         tamaño de página; si es nulo, {@value #TAMANIO_POR_DEFECTO}.
     * @param sort         nombre lógico de la columna a ordenar (clave de {@code ordenables}).
     * @param dir          {@code asc} o {@code desc}.
     * @param ordenables   mapa nombre lógico → propiedad real de la entidad.
     * @param ordenDefecto propiedad real por la que se ordena si no llega {@code sort}.
     * @param dirDefecto   dirección por defecto si no llega {@code dir}.
     * @return el {@link Pageable} listo para el repositorio.
     * @throws BadRequestException si algún valor no es válido.
     */
    public static Pageable crear(Integer page, Integer size, String sort, String dir,
                                 Map<String, String> ordenables, String ordenDefecto,
                                 Sort.Direction dirDefecto) {
        int pagina = page == null ? 0 : page;
        int tamanio = size == null ? TAMANIO_POR_DEFECTO : size;
        if (pagina < 0) {
            throw new BadRequestException("La página debe ser un número mayor o igual a cero.");
        }
        if (tamanio < 1 || tamanio > TAMANIO_MAXIMO) {
            throw new BadRequestException("El tamaño de página debe estar entre 1 y " + TAMANIO_MAXIMO + ".");
        }
        String propiedad = ordenDefecto;
        if (sort != null && !sort.trim().isEmpty()) {
            propiedad = ordenables.get(sort.trim());
            if (propiedad == null) {
                throw new BadRequestException("No se puede ordenar por la columna '" + sort.trim() + "'.");
            }
        }
        Sort.Direction direccion = dirDefecto;
        if (dir != null && !dir.trim().isEmpty()) {
            if (dir.trim().equalsIgnoreCase("asc")) {
                direccion = Sort.Direction.ASC;
            } else if (dir.trim().equalsIgnoreCase("desc")) {
                direccion = Sort.Direction.DESC;
            } else {
                throw new BadRequestException("La dirección del orden debe ser 'asc' o 'desc'.");
            }
        }
        // El id como segundo criterio hace estable el orden entre páginas.
        return PageRequest.of(pagina, tamanio, Sort.by(direccion, propiedad).and(Sort.by(direccion, "id")));
    }

    /**
     * Limpia un texto de búsqueda.
     *
     * @param texto texto recibido.
     * @return el texto sin espacios en los extremos en minúsculas, o {@code null} si está vacío.
     */
    public static String limpiar(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            return null;
        }
        return texto.trim().toLowerCase();
    }

    /**
     * Predicado "el campo contiene el texto" sin distinguir mayúsculas.
     *
     * @param cb      constructor de criterios.
     * @param campo   expresión de texto a comparar.
     * @param termino texto ya limpiado con {@link #limpiar(String)}.
     * @return el predicado LIKE.
     */
    public static Predicate contiene(CriteriaBuilder cb, Expression<String> campo, String termino) {
        return cb.like(cb.lower(campo), "%" + termino + "%");
    }
}
