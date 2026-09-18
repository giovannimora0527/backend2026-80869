package com.uniminuto.clinica.service;

import com.uniminuto.clinica.entity.Medico;

import java.util.List;

/**
 * Interfaz que define los servicios para manejar a los medicos.
 */
public interface MedicoService {
    /**
     * Lista medicos.
     * @return lista de medicos con especializaciones
     */
    List<Medico> listarMedicosConEspecializaciones();
}
