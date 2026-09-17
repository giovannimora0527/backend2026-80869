package com.uniminuto.clinica.serviceimpl;

import com.uniminuto.clinica.entity.Medico;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.repository.MedicoRepository;
import com.uniminuto.clinica.service.MedicoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

/**
 * Implementación de la lógica de negocio para {@link MedicoService}.
 */
@Service
public class MedicoServiceImpl implements MedicoService {

    /**
     * Repositorio inyectado para realizar las operaciones sobre la entidad Médico.
     */
    @Autowired
    private MedicoRepository medicoRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<Medico> listarMedicosConEspecializaciones() throws BadRequestException {
        try {
            List<Medico> medicos = medicoRepository.obtenerMedicosConEspecializaciones();

            // Blindaje contra retornos nulos
            if (medicos == null) {
                return Collections.emptyList();
            }

            return medicos;
        } catch (Exception e) {
            throw new BadRequestException("Error al recuperar el listado de médicos y especializaciones: " + e.getMessage());
        }
    }
}