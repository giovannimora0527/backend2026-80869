package com.uniminuto.clinica.service;

import com.uniminuto.clinica.entity.Medico;
import com.uniminuto.clinica.exception.BadRequestException;

import java.util.List;

/**
 * Interfaz de servicio para las operaciones de negocio relacionadas con los médicos.
 */
public interface MedicoService {

    /**
     * Obtiene el listado completo de médicos asociando sus especializaciones médicas.
     *
     * @return Lista de médicos con especializaciones.
     * @throws BadRequestException Si ocurre un error durante el procesamiento de la consulta.
     */
    List<Medico> listarMedicosConEspecializaciones() throws BadRequestException;
}