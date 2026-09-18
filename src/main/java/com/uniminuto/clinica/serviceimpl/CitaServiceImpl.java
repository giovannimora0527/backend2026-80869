package com.uniminuto.clinica.serviceimpl;

import com.uniminuto.clinica.entity.Cita;
import com.uniminuto.clinica.models.CitaRQ;
import com.uniminuto.clinica.models.CitaRS;
import com.uniminuto.clinica.repository.CitaRepository;
import com.uniminuto.clinica.service.CitaService;
import com.uniminuto.clinica.exception.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CitaServiceImpl implements CitaService {

    /** Repositorio para acceder a los datos de (Cita) */
    @Autowired
    private CitaRepository citaRepository;

    /**
     * Valida que la fecha inicial no sea posterior a la fecha final antes
     * de consultar, para evitar rangos de fecha inválidos.
     */
    @Override
    public List<CitaRS> filtrarPorRangoFechas(LocalDateTime fechaInicio, LocalDateTime fechaFin) throws BadRequestException {
        if (fechaInicio == null || fechaFin == null) {
            throw new BadRequestException("Debe indicar fechaInicio y fechaFin");
        }
        if (fechaInicio.isAfter(fechaFin)) {
            throw new BadRequestException("fechaInicio no puede ser posterior a fechaFin");
        }
        return citaRepository.findByFechaHoraBetweenOrderByFechaHoraDesc(fechaInicio, fechaFin)
                .stream()
                .map(this::convertirARS)
                .collect(Collectors.toList());
    }

    @Override
    public CitaRS crear(CitaRQ request) throws BadRequestException {
        validarDatos(request);

        Cita cita = new Cita();
        cita.setClienteId(request.getClienteId());
        cita.setMascotaId(request.getMascotaId());
        cita.setMedicoId(request.getMedicoId());
        cita.setFechaHora(request.getFechaHora());
        cita.setEstado(request.getEstado());
        cita.setMotivo(request.getMotivo());

        cita = citaRepository.save(cita);
        return convertirARS(cita);
    }

    @Override
    public CitaRS actualizar(CitaRQ request) throws BadRequestException {
        if (request.getId() == null) {
            throw new BadRequestException("id es obligatorio para actualizar la cita");
        }
        Cita cita = citaRepository.findById(request.getId())
                .orElseThrow(() -> new BadRequestException("No existe la cita con id " + request.getId()));

        validarDatos(request);

        cita.setClienteId(request.getClienteId());
        cita.setMascotaId(request.getMascotaId());
        cita.setMedicoId(request.getMedicoId());
        cita.setFechaHora(request.getFechaHora());
        cita.setEstado(request.getEstado());
        cita.setMotivo(request.getMotivo());

        cita = citaRepository.save(cita);
        return convertirARS(cita);
    }

    /**
     * Valida que los campos obligatorios de una cita vengan diligenciados.
     */
    private void validarDatos(CitaRQ request) throws BadRequestException {
        if (request.getClienteId() == null || request.getMascotaId() == null || request.getMedicoId() == null) {
            throw new BadRequestException("clienteId, mascotaId y medicoId son obligatorios");
        }
        if (request.getFechaHora() == null) {
            throw new BadRequestException("fechaHora es obligatoria");
        }
        if (request.getEstado() == null || request.getEstado().isBlank()) {
            throw new BadRequestException("estado es obligatorio");
        }
    }

    /**
     * Convierte una entidad (Cita)en su DTO de salida.
     */
    private CitaRS convertirARS(Cita cita) {
        CitaRS rs = new CitaRS();
        rs.setId(cita.getId());
        rs.setClienteId(cita.getClienteId());
        rs.setMascotaId(cita.getMascotaId());
        rs.setMedicoId(cita.getMedicoId());
        rs.setFechaHora(cita.getFechaHora());
        rs.setEstado(cita.getEstado());
        rs.setMotivo(cita.getMotivo());
        return rs;
    }
}