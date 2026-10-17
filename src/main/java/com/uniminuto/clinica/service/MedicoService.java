package com.uniminuto.clinica.service;

import com.uniminuto.clinica.entity.Medico;
import java.util.List;

/**
 * Interfaz que define los servicios de negocio para los Médicos Veterinarios.
 *
 * @author Yulian Estephania Montealegre
 */
public interface MedicoService {

    /**
     * Lista todos los médicos registrados en el sistema.
     *
     * @return Lista de entidades Medico.
     */
    List<Medico> listarMedicos();

    /**
     * Busca un médico por su ID único.
     *
     * @param id Identificador del médico.
     * @return Entidad Medico encontrada.
     */
    Medico buscarMedicoPorId(Long id);
}