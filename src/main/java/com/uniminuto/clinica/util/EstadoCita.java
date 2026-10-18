package com.uniminuto.clinica.util;

import java.util.Map;
import java.util.Set;

/**
 * Estados válidos de una cita y reglas de transición entre ellos.
 * Centraliza el ciclo de vida de la cita para que todos los servicios usen las mismas reglas.
 */
public final class EstadoCita {

    /** Cita registrada, aún sin programar ni confirmar. */
    public static final String PENDIENTE = "PENDIENTE";

    /** Cita con fecha y veterinario asignados. */
    public static final String PROGRAMADA = "PROGRAMADA";

    /** Cita confirmada por el cliente. */
    public static final String CONFIRMADA = "CONFIRMADA";

    /** Cita ya realizada; es un estado final e inmutable. */
    public static final String ATENDIDA = "ATENDIDA";

    /** Cita cancelada; solo puede revivirse con la operación explícita de reactivación. */
    public static final String CANCELADA = "CANCELADA";

    /** Todos los estados admitidos. */
    public static final Set<String> TODOS = Set.of(PENDIENTE, PROGRAMADA, CONFIRMADA, ATENDIDA, CANCELADA);

    /** Estados con los que se permite crear una cita nueva (una cita futura no puede nacer atendida ni cancelada). */
    public static final Set<String> INICIALES = Set.of(PENDIENTE, PROGRAMADA, CONFIRMADA);

    /** Transiciones permitidas: estado origen -> estados destino. */
    private static final Map<String, Set<String>> TRANSICIONES = Map.of(
            PENDIENTE, Set.of(PROGRAMADA, CONFIRMADA, CANCELADA),
            PROGRAMADA, Set.of(CONFIRMADA, ATENDIDA, CANCELADA),
            CONFIRMADA, Set.of(ATENDIDA, CANCELADA),
            ATENDIDA, Set.of(),
            CANCELADA, Set.of());

    /** Clase utilitaria: no se instancia. */
    private EstadoCita() {
    }

    /**
     * Normaliza un estado (recorta espacios y pasa a mayúsculas).
     *
     * @param estado Texto recibido del cliente; puede ser nulo.
     * @return Estado normalizado o {@code null} si el texto era nulo o vacío.
     */
    public static String normalizar(String estado) {
        if (estado == null || estado.isBlank()) {
            return null;
        }
        return estado.trim().toUpperCase();
    }

    /**
     * Indica si el estado pertenece al catálogo de estados de cita.
     *
     * @param estado Estado ya normalizado.
     * @return {@code true} si es un estado conocido.
     */
    public static boolean esValido(String estado) {
        return estado != null && TODOS.contains(estado);
    }

    /**
     * Indica si se puede pasar del estado origen al estado destino.
     *
     * @param origen  Estado actual de la cita.
     * @param destino Estado solicitado.
     * @return {@code true} si la transición está permitida.
     */
    public static boolean permiteTransicion(String origen, String destino) {
        return TRANSICIONES.getOrDefault(origen, Set.of()).contains(destino);
    }
}
