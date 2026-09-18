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
 * Implementacion de los servicios de negocio para la entidad AnotacionHistoria.
 */
@Service
public class AnotacionHistoriaServiceImpl implements AnotacionHistoriaService {

    @Autowired
    private AnotacionHistoriaRepository anotacionRepository;

    @Override
    @Transactional
    public AnotacionHistoria crearAnotacion(AnotacionHistoria anotacion) throws BadRequestException {
        if (anotacion.getDescripcion() == null || anotacion.getDescripcion().trim().isEmpty()) {
            throw new BadRequestException("La descripcion de la anotacion es obligatoria.");
        }
        return anotacionRepository.save(anotacion);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AnotacionHistoria> listarAnotacionesPorFecha(LocalDateTime fechaInicio, LocalDateTime fechaFin) throws BadRequestException {
        if (fechaInicio == null) throw new BadRequestException("La fecha inicial no puede ser nula.");
        if (fechaFin == null) throw new BadRequestException("La fecha final no puede ser nula.");
        if (fechaInicio.isAfter(fechaFin)) throw new BadRequestException("La fecha inicial no puede ser posterior a la fecha final.");

        return anotacionRepository.findByFechaBetweenOrderByFechaDesc(fechaInicio, fechaFin);
    }

    @Override
    @Transactional
    public AnotacionHistoria actualizarAnotacion(Long id, AnotacionHistoria anotacion) throws BadRequestException {
        AnotacionHistoria existente = anotacionRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("La anotacion con ID " + id + " no existe."));

        if (anotacion.getDescripcion() == null || anotacion.getDescripcion().trim().isEmpty()) {
            throw new BadRequestException("La descripcion es obligatoria.");
        }

        existente.setDescripcion(anotacion.getDescripcion());
        existente.setFecha(anotacion.getFecha());
        return anotacionRepository.save(existente);
    }
}