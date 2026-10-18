package com.uniminuto.clinica.util;

/** Estados válidos de una historia clínica. */
public final class EstadoHistoria {

    /** Historia abierta: admite nuevas anotaciones. */
    public static final String ACTIVA = "ACTIVA";

    /** Historia cerrada: ya no admite anotaciones. */
    public static final String CERRADA = "CERRADA";

    /** Clase utilitaria: no se instancia. */
    private EstadoHistoria() {
    }
}
