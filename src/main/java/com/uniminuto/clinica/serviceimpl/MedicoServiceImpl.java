package com.uniminuto.clinica.serviceimpl;

import com.uniminuto.clinica.entity.Medico;
import com.uniminuto.clinica.exception.ResourceNotFoundException;
import com.uniminuto.clinica.repository.MedicoRepository;
import com.uniminuto.clinica.service.MedicoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementación de la lógica de negocio para la gestión de Médicos Veterinarios.
 *
 * @author Yulian Estephania Montealegre
 */
@Service
public class MedicoServiceImpl implements MedicoService {

    @Autowired
    private MedicoRepository medicoRepository;

    /**
     * Lista todos los médicos de la veterinaria.
     *
     * @return Lista completa de médicos.
     */
    @Override
    public List<Medico> listarMedicos() {
        return medicoRepository.findAll();
    }

    /**
     * Busca un médico por ID y lanza excepción 404 si no existe.
     *
     * @param id Identificador del médico.
     * @return Entidad Medico.
     */
    @Override
    public Medico buscarMedicoPorId(Long id) {
        return medicoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("El médico con ID " + id + " no fue encontrado."));
    }
}