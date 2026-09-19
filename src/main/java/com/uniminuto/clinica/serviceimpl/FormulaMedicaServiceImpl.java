package com.uniminuto.clinica.serviceimpl;

import com.uniminuto.clinica.entity.FormulaMedica;
import com.uniminuto.clinica.models.FormulaMedicaRS;
import com.uniminuto.clinica.repository.FormulaMedicaRepository;
import com.uniminuto.clinica.service.FormulaMedicaService;
import com.uniminuto.clinica.exception.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FormulaMedicaServiceImpl implements FormulaMedicaService {

    /** Repositorio para acceder a los datos de (FormulaMedica). */
    @Autowired
    private FormulaMedicaRepository formulaMedicaRepository;

    @Override
    public List<FormulaMedicaRS> listar() throws BadRequestException {
        return formulaMedicaRepository.findAllByOrderByFechaCreacionRegistroDesc()
                .stream()
                .map(this::convertirARS)
                .collect(Collectors.toList());
    }

    /**
     * Convierte una entidad (FormulaMedica) en su DTO de salida.
     */
    private FormulaMedicaRS convertirARS(FormulaMedica formula) {
        FormulaMedicaRS rs = new FormulaMedicaRS();
        rs.setId(formula.getId());
        rs.setCitaId(formula.getCitaId());
        rs.setMedicamentoId(formula.getMedicamentoId());
        rs.setDosis(formula.getDosis());
        rs.setIndicaciones(formula.getIndicaciones());
        rs.setFechaCreacionRegistro(formula.getFechaCreacionRegistro());
        rs.setFechaActualizacionRegistro(formula.getFechaActualizacionRegistro());
        return rs;
    }
}