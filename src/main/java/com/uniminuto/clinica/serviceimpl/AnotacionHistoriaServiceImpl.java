package com.uniminuto.clinica.serviceimpl;

import com.uniminuto.clinica.entity.AnotacionHistoria;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.repository.AnotacionHistoriaRepository;
import com.uniminuto.clinica.service.AnotacionHistoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Implementación de la lógica de negocio para {@link AnotacionHistoriaService}.
 */
@Service
public class AnotacionHistoriaServiceImpl implements AnotacionHistoriaService {

    @Autowired
    private AnotacionHistoriaRepository repository;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public AnotacionHistoria crear(AnotacionHistoria anotacion) throws BadRequestException {
        if (anotacion == null) {
            throw new BadRequestException("El cuerpo de la anotación no puede ser nulo.");
        }
        if (anotacion.getObservacion() == null || anotacion.getObservacion().trim().isEmpty()) {
            throw new BadRequestException("La observación de la historia médica es obligatoria y no puede estar vacía.");
        }
        return repository.save(anotacion);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public AnotacionHistoria actualizar(Long id, AnotacionHistoria anotacion) throws BadRequestException {
        if (id == null) {
            throw new BadRequestException("El ID de la anotación es obligatorio.");
        }
        if (anotacion == null) {
            throw new BadRequestException("Los datos a actualizar no pueden ser nulos.");
        }
        if (anotacion.getObservacion() == null || anotacion.getObservacion().trim().isEmpty()) {
            throw new BadRequestException("La observación actualizada no puede estar vacía.");
        }

        AnotacionHistoria existente = repository.findById(id)
                .orElseThrow(() -> new BadRequestException("No se encontró ninguna anotación con el ID: " + id));

        existente.setObservacion(anotacion.getObservacion());
        return repository.save(existente);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<AnotacionHistoria> listarPorFechas(LocalDateTime fechaInicio, LocalDateTime fechaFin) throws BadRequestException {
        if ((fechaInicio != null && fechaFin == null) || (fechaInicio == null && fechaFin != null)) {
            throw new BadRequestException("Para filtrar por rango debe enviar ambas fechas (fechaInicio y fechaFin).");
        }

        if (fechaInicio != null && fechaFin != null) {
            if (fechaInicio.isAfter(fechaFin)) {
                throw new BadRequestException("La fecha inicial no puede ser posterior a la fecha final.");
            }
            return repository.findByFechaCreacionBetweenOrderByFechaCreacionDesc(fechaInicio, fechaFin);
        }
        return repository.findAllByOrderByFechaCreacionDesc();
    }
}