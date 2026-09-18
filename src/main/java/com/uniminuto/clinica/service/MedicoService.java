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
     */
    List<Medico> listarMedicosConEspecializaciones() throws BadRequestException;
}