package com.uniminuto.clinica.util;

import com.uniminuto.clinica.entity.Cliente;
import com.uniminuto.clinica.entity.Medico;

/** Utilidades para construir nombres legibles a partir de las entidades. */
public final class NombreUtil {

    /** Clase utilitaria: no se instancia. */
    private NombreUtil() {
    }

    /**
     * Nombre completo de un cliente.
     *
     * @param cliente Cliente (puede ser nulo).
     * @return "Nombres Apellidos" o {@code null} si el cliente es nulo.
     */
    public static String completo(Cliente cliente) {
        return cliente == null ? null : (cliente.getNombres() + " " + cliente.getApellidos()).trim();
    }

    /**
     * Nombre completo de un veterinario.
     *
     * @param medico Veterinario (puede ser nulo).
     * @return "Nombres Apellidos" o {@code null} si el médico es nulo.
     */
    public static String completo(Medico medico) {
        return medico == null ? null : (medico.getNombres() + " " + medico.getApellidos()).trim();
    }
}
