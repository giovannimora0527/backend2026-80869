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

    @Autowired
    private CitaRepository citaRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Cita> listarCitas() throws BadRequestException {
        return citaRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Cita> filtrarCitasPorFecha(LocalDateTime fechaInicio, LocalDateTime fechaFin) throws BadRequestException {
        if (fechaInicio == null) throw new BadRequestException("La fecha inicial no puede ser nula.");
        if (fechaFin == null) throw new BadRequestException("La fecha final no puede ser nula.");
        if (fechaInicio.isAfter(fechaFin)) throw new BadRequestException("La fecha inicial no puede ser posterior a la fecha final.");

        return citaRepository.findByFechaHoraBetweenOrderByFechaHoraDesc(fechaInicio, fechaFin);
    }

    @Override
    @Transactional
    public Cita crearCita(Cita cita) throws BadRequestException {
        if (cita.getFechaHora() == null) throw new BadRequestException("La fecha y hora son obligatorias.");
        if (cita.getMotivo() == null || cita.getMotivo().trim().isEmpty()) throw new BadRequestException("El motivo es obligatorio.");
        if (cita.getEstado() == null || cita.getEstado().trim().isEmpty()) throw new BadRequestException("El estado es obligatorio.");
        return citaRepository.save(cita);
    }

    @Override
    @Transactional
    public Cita actualizarCita(Long id, Cita cita) throws BadRequestException {
        Cita existente = citaRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("La cita con ID " + id + " no existe."));

        if (cita.getFechaHora() == null) throw new BadRequestException("La fecha y hora son obligatorias.");
        if (cita.getMotivo() == null || cita.getMotivo().trim().isEmpty()) throw new BadRequestException("El motivo es obligatorio.");

        existente.setFechaHora(cita.getFechaHora());
        existente.setMotivo(cita.getMotivo());
        existente.setEstado(cita.getEstado());
        return citaRepository.save(existente);
    }
}