package com.uniminuto.clinica.service.impl;

import com.uniminuto.clinica.entity.FormulaMedica;
import com.uniminuto.clinica.repository.FormulaMedicaRepository;
import com.uniminuto.clinica.service.FormulaMedicaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Implementación del servicio de fórmulas médicas.
 * @author Yulian Montealegre
 */
@Service  //  ESTA ANOTACIÓN ES OBLIGATORIA
public class FormulaMedicaServiceImpl implements FormulaMedicaService {

    @Autowired
    private FormulaMedicaRepository repository;

    @Override
    public List<FormulaMedica> obtenerTodas() {
        return repository.findAllByOrderByFechaCreacionRegistroDesc();
    }

    @Override
    public FormulaMedica crear(FormulaMedica formula) {
        formula.setFechaCreacionRegistro(LocalDateTime.now());
        formula.setFechaActualizacionRegistro(LocalDateTime.now());
        return repository.save(formula);
    }

    @Override
    public FormulaMedica actualizar(Long id, FormulaMedica formula) {
        FormulaMedica existente = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Fórmula médica no encontrada con ID: " + id));

        existente.setCitaId(formula.getCitaId());
        existente.setMedicamentoId(formula.getMedicamentoId());
        existente.setDosis(formula.getDosis());
        existente.setIndicaciones(formula.getIndicaciones());
        existente.setNombreMedicamento(formula.getNombreMedicamento());
        existente.setPresentacion(formula.getPresentacion());
        existente.setDescripcion(formula.getDescripcion());
        existente.setFechaActualizacionRegistro(LocalDateTime.now());

        return repository.save(existente);
    }
}