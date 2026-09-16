package com.uniminuto.clinica.service;

import com.uniminuto.clinica.entity.FormulaMedica;
import java.util.List;

/**
 * Servicio para la gestion de operaciones de negocio relativas a las formulas medicas.
 */
public interface FormulaMedicaService {

    /**
     * Recupera todas las formulas medicas ordenadas por fecha de creacion descendente.
     *
     * @return Lista de formulas medicas.
     */
    List<FormulaMedica> obtenerTodasOrdenadas();
}