package com.uniminuto.clinica.serviceimpl;

import com.uniminuto.clinica.entity.Cita;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.repository.CitaRepository;
import com.uniminuto.clinica.service.CitaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Implementacion de los servicios de negocio para la entidad Cita.
 */
@Service
public class CitaServiceImpl implements CitaService {

    /**
     * Repositorio inyectado para la persistencia de Cita.
     */
    @Autowired
    private CitaRepository citaRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<Cita> listarCitas() throws BadRequestException {
        return citaRepository.findAll();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<Cita> filtrarCitasPorFecha(LocalDateTime fechaInicio, LocalDateTime fechaFin) throws BadRequestException {
        if (fechaInicio == null) {
            throw new BadRequestException("La fecha inicial no puede ser nula.");
        }
        if (fechaFin == null) {
            throw new BadRequestException("La fecha final no puede ser nula.");
        }
        if (fechaInicio.isAfter(fechaFin)) {
            throw new BadRequestException("La fecha inicial no puede ser posterior a la fecha final.");
        }

        return citaRepository.findByFechaHoraBetweenOrderByFechaHoraDesc(fechaInicio, fechaFin);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public Cita crearCita(Cita cita) throws BadRequestException {
        if (cita == null) {
            throw new BadRequestException("Los datos de la cita no pueden ser nulos.");
        }
        return citaRepository.save(cita);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public Cita actualizarCita(Long id, Cita citaDetalles) throws BadRequestException {
        if (id == null) {
            throw new BadRequestException("El ID de la cita es obligatorio.");
        }
        if (citaDetalles == null) {
            throw new BadRequestException("Los datos para actualizar no pueden ser nulos.");
        }

        Cita citaExistente = citaRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("No se encontro ninguna cita con el ID: " + id));

        citaExistente.setFechaHora(citaDetalles.getFechaHora());
        citaExistente.setMotivo(citaDetalles.getMotivo());
        citaExistente.setEstado(citaDetalles.getEstado());

        return citaRepository.save(citaExistente);
    }
}