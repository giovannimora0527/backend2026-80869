package com.uniminuto.clinica.service;

import com.uniminuto.clinica.entity.Medico;
import com.uniminuto.clinica.exception.BadRequestException;
import java.util.List;

/**
 * Interfaz de servicio para la gestion de medicos.
 */
public interface MedicoService {

    /**
     * Lista todos los medicos con sus especializaciones.
     *
     * @return Lista de medicos con sus especializaciones cargadas.
     * @throws BadRequestException Si ocurre un error de negocio.
     */
    List<Medico> listarMedicosConEspecializaciones() throws BadRequestException;
}