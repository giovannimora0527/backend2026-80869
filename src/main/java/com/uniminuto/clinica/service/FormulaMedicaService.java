package com.uniminuto.clinica.service;

import com.uniminuto.clinica.entity.FormulaMedica;
import com.uniminuto.clinica.exception.BadRequestException;
import java.util.List;

/**
 * Servicio para la gestion de operaciones de negocio relativas a las formulas medicas.
 */
public interface FormulaMedicaService {

    /**
     * Recupera todas las formulas medicas ordenadas por fecha de creacion descendente.
     * @return Lista de formulas medicas.
     * @throws BadRequestException Si ocurre un error de negocio.
     */
    List<FormulaMedica> obtenerTodasOrdenadas() throws BadRequestException;
}