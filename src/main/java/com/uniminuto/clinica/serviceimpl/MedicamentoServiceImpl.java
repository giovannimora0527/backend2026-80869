package com.uniminuto.clinica.serviceimpl;

import com.uniminuto.clinica.entity.Medicamento;
import com.uniminuto.clinica.repository.MedicamentoRepository;
import com.uniminuto.clinica.service.MedicamentoService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import org.springframework.stereotype.Service;

/**
 * Implementacion del servicio de medicamentos.
 */
@Service
public class MedicamentoServiceImpl implements MedicamentoService {

    @Autowired
    private MedicamentoRepository repository;


    @Override
    public List<Medicamento> listarInventarioOrdenado() {
        return repository.findAllByOrderByFechaCreacionRegistroDesc();
    }
}
