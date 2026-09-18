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
 * Incluye validaciones y control de errores para garantizar la integridad de los datos.
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
     * Implementa blindaje contra fechas nulas o incoherentes.
     */
    @Override
    @Transactional(readOnly = true)
    public List<Cita> filtrarCitasPorFecha(LocalDateTime fechaInicio, LocalDateTime fechaFin) throws BadRequestException {
        // Validación 1: fechaInicio no puede ser null
        if (fechaInicio == null) {
            throw new BadRequestException("La fecha inicial no puede ser nula.");
        }

        // Validación 2: fechaFin no puede ser null
        if (fechaFin == null) {
            throw new BadRequestException("La fecha final no puede ser nula.");
        }

        // Validación 3: fechaInicio no puede ser mayor que fechaFin
        if (fechaInicio.isAfter(fechaFin)) {
            throw new BadRequestException("La fecha inicial no puede ser posterior a la fecha final.");
        }

        // Si pasa todas las validaciones, consultamos la base de datos
        return citaRepository.findByFechaHoraBetweenOrderByFechaHoraDesc(fechaInicio, fechaFin);
    }

    /**
     * {@inheritDoc}
     * Implementa blindaje contra datos obligatorios nulos.
     */
    @Override
    @Transactional
    public Cita crearCita(Cita cita) throws BadRequestException {
        // Validación 1: Fecha y hora son obligatorias
        if (cita.getFechaHora() == null) {
            throw new BadRequestException("La fecha y hora de la cita son obligatorias.");
        }

        // Validación 2: Motivo es obligatorio (no null y no vacío)
        if (cita.getMotivo() == null || cita.getMotivo().trim().isEmpty()) {
            throw new BadRequestException("El motivo de la cita es obligatorio.");
        }

        // Validación 3: Estado es obligatorio (no null y no vacío)
        if (cita.getEstado() == null || cita.getEstado().trim().isEmpty()) {
            throw new BadRequestException("El estado de la cita es obligatorio.");
        }

        // Validación 4: Cliente es obligatorio
        if (cita.getCliente() == null) {
            throw new BadRequestException("El cliente de la cita es obligatorio.");
        }

        // Validación 5: Mascota es obligatoria
        if (cita.getMascota() == null) {
            throw new BadRequestException("La mascota de la cita es obligatoria.");
        }

        // Validación 6: Médico es obligatorio
        if (cita.getMedico() == null) {
            throw new BadRequestException("El médico de la cita es obligatorio.");
        }

        // Si pasa todas las validaciones, guardamos en la base de datos
        return citaRepository.save(cita);
    }

    /**
     * {@inheritDoc}
     * Implementa blindaje verificando que la cita exista antes de actualizar.
     */
    @Override
    @Transactional
    public Cita actualizarCita(Long id, Cita cita) throws BadRequestException {
        // Validación 1: Buscar si la cita existe
        Cita citaExistente = citaRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("La cita con ID " + id + " no existe en el sistema."));

        // Validación 2: Fecha y hora son obligatorias
        if (cita.getFechaHora() == null) {
            throw new BadRequestException("La fecha y hora de la cita son obligatorias.");
        }

        // Validación 3: Motivo es obligatorio
        if (cita.getMotivo() == null || cita.getMotivo().trim().isEmpty()) {
            throw new BadRequestException("El motivo de la cita es obligatorio.");
        }

        // Validación 4: Estado es obligatorio
        if (cita.getEstado() == null || cita.getEstado().trim().isEmpty()) {
            throw new BadRequestException("El estado de la cita es obligatorio.");
        }

        // Actualizamos solo los campos que el usuario puede modificar
        citaExistente.setFechaHora(cita.getFechaHora());
        citaExistente.setMotivo(cita.getMotivo());
        citaExistente.setEstado(cita.getEstado());
        citaExistente.setCliente(cita.getCliente());
        citaExistente.setMascota(cita.getMascota());
        citaExistente.setMedico(cita.getMedico());

        // Guardamos los cambios
        return citaRepository.save(citaExistente);
    }
}