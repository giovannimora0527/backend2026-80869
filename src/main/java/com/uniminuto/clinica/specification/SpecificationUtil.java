package com.uniminuto.clinica.specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;

/** Utilidades para construir predicados de filtros dinámicos. */
public final class SpecificationUtil {

    /** Carácter de escape usado en los LIKE (evita que % y _ del usuario actúen como comodines). */
    private static final char ESCAPE = (char) 33;

    /** Clase utilitaria: no se instancia. */
    private SpecificationUtil() {
    }

    /**
     * Predicado "contiene" sin distinguir mayúsculas.
     *
     * @param cb    Constructor de criterios.
     * @param campo Expresión de texto a evaluar.
     * @param texto Texto buscado.
     * @return Predicado LIKE %texto%.
     */
    public static Predicate contiene(CriteriaBuilder cb, Expression<String> campo, String texto) {
        String limpio = texto.trim().toLowerCase()
                .replace("!", "!!").replace("%", "!%").replace("_", "!_");
        return cb.like(cb.lower(campo), "%" + limpio + "%", ESCAPE);
    }

    /**
     * Expresión "Nombres Apellidos" para buscar por nombre completo.
     *
     * @param cb        Constructor de criterios.
     * @param nombres   Expresión de los nombres.
     * @param apellidos Expresión de los apellidos.
     * @return Concatenación con un espacio.
     */
    public static Expression<String> nombreCompleto(CriteriaBuilder cb, Expression<String> nombres, Expression<String> apellidos) {
        return cb.concat(cb.concat(nombres, " "), apellidos);
    }
}
