package com.uniminuto.clinica.serviceimpl;

import com.uniminuto.clinica.entity.Medico;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.repository.MedicoRepository;
import com.uniminuto.clinica.service.MedicoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/**
 * Implementacion de los servicios de negocio para la entidad Medico.
 * Incluye la logica para listar medicos con sus especializaciones.
 */
@Service
public class MedicoServiceImpl implements MedicoService {

    /**
     * Repositorio inyectado para la persistencia de Medico.
     */
    @Autowired
    private MedicoRepository medicoRepository;

    /**
     * {@inheritDoc}
     * Gracias a FetchType.EAGER en la entidad Medico,
     * las especializaciones se cargan automaticamente al ejecutar findAll().
     */
    @Override
    @Transactional(readOnly = true)
    public List<Medico> listarMedicosConEspecializaciones() throws BadRequestException {
        return medicoRepository.findAll();
    }
}