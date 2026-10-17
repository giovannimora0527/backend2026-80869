package com.uniminuto.clinica.serviceimpl;

import com.uniminuto.clinica.entity.Medicamento;
import com.uniminuto.clinica.exception.ResourceNotFoundException;
import com.uniminuto.clinica.repository.MedicamentoRepository;
import com.uniminuto.clinica.service.MedicamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementación de la lógica de negocio para la gestión de Medicamentos.
 *
 * @author Yulian Estephania Montealegre
 */
@Service
public class MedicamentoServiceImpl implements MedicamentoService {

    @Autowired
    private MedicamentoRepository medicamentoRepository;

    /**
     * Lista todos los medicamentos registrados.
     *
     * @return Lista completa de medicamentos.
     */
    @Override
    public List<Medicamento> listarMedicamentos() {
        return medicamentoRepository.findAll();
    }

    /**
     * Busca un medicamento por ID y lanza excepción 404 si no existe.
     *
     * @param id Identificador del medicamento.
     * @return Entidad Medicamento.
     */
    @Override
    public Medicamento buscarMedicamentoPorId(Long id) {
        return medicamentoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("El medicamento con ID " + id + " no fue encontrado."));
    }
}