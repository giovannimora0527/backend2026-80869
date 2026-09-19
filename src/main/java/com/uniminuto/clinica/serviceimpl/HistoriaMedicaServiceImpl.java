package com.uniminuto.clinica.serviceimpl;

import com.uniminuto.clinica.entity.HistoriaMedica;
import com.uniminuto.clinica.models.HistoriaMedicaRQ;
import com.uniminuto.clinica.models.HistoriaMedicaRS;
import com.uniminuto.clinica.repository.HistoriaMedicaRepository;
import com.uniminuto.clinica.service.HistoriaMedicaService;
import com.uniminuto.clinica.exception.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class HistoriaMedicaServiceImpl implements HistoriaMedicaService {

    /** Repositorio para acceder a los datos de (HistoriaMedica). */
    @Autowired
    private HistoriaMedicaRepository historiaMedicaRepository;

    /**
     * Valida que la fecha inicial no sea posterior a la fecha final antes
     * de consultar, para evitar rangos de fecha inválidos.
     */
    @Override
    public List<HistoriaMedicaRS> listar(LocalDateTime fechaInicial, LocalDateTime fechaFinal) throws BadRequestException {
        if (fechaInicial == null || fechaFinal == null) {
            throw new BadRequestException("Debe indicar fechaInicial y fechaFinal");
        }
        if (fechaInicial.isAfter(fechaFinal)) {
            throw new BadRequestException("fechaInicial no puede ser posterior a fechaFinal");
        }
        return historiaMedicaRepository.findByFechaCreacionBetweenOrderByFechaCreacionDesc(fechaInicial, fechaFinal)
                .stream()
                .map(this::convertirARS)
                .collect(Collectors.toList());
    }

    @Override
    public HistoriaMedicaRS obtenerPorId(Long id) throws BadRequestException {
        HistoriaMedica historia = historiaMedicaRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("No existe la historia médica con id " + id));
        return convertirARS(historia);
    }

    @Override
    public HistoriaMedicaRS crear(HistoriaMedicaRQ request) throws BadRequestException {
        if (request.getPacienteId() == null) {
            throw new BadRequestException("pacienteId es obligatorio");
        }

        HistoriaMedica historia = new HistoriaMedica();
        historia.setPacienteId(request.getPacienteId());
        historia.setFechaCreacion(LocalDateTime.now());

        historia = historiaMedicaRepository.save(historia);
        return convertirARS(historia);
    }

    @Override
    public HistoriaMedicaRS actualizar(HistoriaMedicaRQ request) throws BadRequestException {
        if (request.getId() == null) {
            throw new BadRequestException("id es obligatorio para actualizar la historia médica");
        }
        HistoriaMedica historia = historiaMedicaRepository.findById(request.getId())
                .orElseThrow(() -> new BadRequestException("No existe la historia médica con id " + request.getId()));

        if (request.getPacienteId() == null) {
            throw new BadRequestException("pacienteId es obligatorio");
        }
        historia.setPacienteId(request.getPacienteId());

        historia = historiaMedicaRepository.save(historia);
        return convertirARS(historia);
    }

    @Override
    public void eliminar(Long id) throws BadRequestException {
        if (!historiaMedicaRepository.existsById(id)) {
            throw new BadRequestException("No existe la historia médica con id " + id);
        }
        historiaMedicaRepository.deleteById(id);
    }

    /**
     * Convierte una entidad (HistoriaMedica) en su DTO de salida.
     */
    private HistoriaMedicaRS convertirARS(HistoriaMedica historia) {
        HistoriaMedicaRS rs = new HistoriaMedicaRS();
        rs.setId(historia.getId());
        rs.setPacienteId(historia.getPacienteId());
        rs.setFechaCreacion(historia.getFechaCreacion());
        return rs;
    }
}