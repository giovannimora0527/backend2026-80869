package com.uniminuto.clinica.utils;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Constantes y reglas de los estados de una cita.
 * <p>
 * Se guardan como texto en mayúsculas en la columna {@code cita.estado}.
 * Centralizarlos evita escribir el mismo texto en varios servicios y deja en un
 * solo lugar el diagrama de transiciones permitidas.
 */
public final class EstadoCita {

    /** Cita solicitada que aún no ha sido confirmada. */
    public static final String PENDIENTE = "PENDIENTE";

    /** Cita agendada en el calendario. */
    public static final String PROGRAMADA = "PROGRAMADA";

    /** Cita confirmada con el cliente. */
    public static final String CONFIRMADA = "CONFIRMADA";

    /** Cita ya realizada; su información histórica no se modifica. */
    public static final String ATENDIDA = "ATENDIDA";

    /** Cita cancelada; no se edita ni se reactiva sin una operación explícita. */
    public static final String CANCELADA = "CANCELADA";

    /** Estados en los que la cita todavía está "viva" (ocupa agenda). */
    public static final List<String> ACTIVAS = List.of(PENDIENTE, PROGRAMADA, CONFIRMADA);

    /** Todos los estados válidos. */
    public static final List<String> TODOS = List.of(PENDIENTE, CONFIRMADA, ATENDIDA, CANCELADA, PROGRAMADA);

    /** Estados con los que se puede crear una cita nueva (no nace atendida ni cancelada). */
    public static final List<String> CREABLES = List.of(PENDIENTE, PROGRAMADA, CONFIRMADA);

    /**
     * Cambios de estado permitidos. Una cita atendida o cancelada es un estado final:
     * la cancelada solo vuelve a la agenda con la operación explícita de reactivar.
     */
    private static final Map<String, Set<String>> TRANSICIONES = Map.of(
            PENDIENTE, Set.of(PROGRAMADA, CONFIRMADA, ATENDIDA, CANCELADA),
            PROGRAMADA, Set.of(PENDIENTE, CONFIRMADA, ATENDIDA, CANCELADA),
            CONFIRMADA, Set.of(PENDIENTE, PROGRAMADA, ATENDIDA, CANCELADA),
            ATENDIDA, Set.of(),
            CANCELADA, Set.of());

    private EstadoCita() {
        // Clase de constantes: no se instancia.
    }

    /**
     * Normaliza un estado recibido (quita espacios y pasa a mayúsculas).
     *
     * @param estado texto recibido, puede ser nulo.
     * @return el estado normalizado o {@code null} si venía vacío.
     */
    public static String normalizar(String estado) {
        if (estado == null || estado.trim().isEmpty()) {
            return null;
        }
        return estado.trim().toUpperCase();
    }

    /**
     * Indica si se puede pasar de un estado a otro.
     *
     * @param desde estado actual.
     * @param hacia estado solicitado.
     * @return {@code true} si el cambio está permitido.
     */
    public static boolean transicionPermitida(String desde, String hacia) {
        return TRANSICIONES.getOrDefault(desde, Set.of()).contains(hacia);
    }
}
