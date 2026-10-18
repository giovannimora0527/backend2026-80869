package com.uniminuto.clinica.util;

import com.uniminuto.clinica.exception.BadRequestException;

import java.time.LocalDate;

/** Validaciones reutilizables por los servicios. */
public final class ValidacionUtil {

    /** Clase utilitaria: no se instancia. */
    private ValidacionUtil() {
    }

    /**
     * Verifica que un rango de fechas sea coherente.
     *
     * @param desde Fecha inicial (opcional).
     * @param hasta Fecha final (opcional).
     * @throws BadRequestException si ambas existen y {@code desde} es posterior a {@code hasta}.
     */
    public static void validarRangoFechas(LocalDate desde, LocalDate hasta) {
        if (desde != null && hasta != null && desde.isAfter(hasta)) {
            throw new BadRequestException("La fecha inicial no puede ser posterior a la fecha final.");
        }
    }

    /**
     * Indica si un texto tiene contenido útil.
     *
     * @param texto Texto a evaluar.
     * @return {@code true} si no es nulo ni está en blanco.
     */
    public static boolean tieneTexto(String texto) {
        return texto != null && !texto.isBlank();
    }
}
