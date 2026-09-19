package com.uniminuto.clinica.serviceimpl;

import com.uniminuto.clinica.entity.AnotacionHistoria;
import com.uniminuto.clinica.models.AnotacionHistoriaRQ;
import com.uniminuto.clinica.models.AnotacionHistoriaRS;
import com.uniminuto.clinica.repository.AnotacionHistoriaRepository;
import com.uniminuto.clinica.repository.HistoriaMedicaRepository;
import com.uniminuto.clinica.service.AnotacionHistoriaService;
import com.uniminuto.clinica.exception.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AnotacionHistoriaServiceImpl implements AnotacionHistoriaService {

    /** Repositorio para acceder a los datos de (AnotacionHistoria) */
    @Autowired
    private AnotacionHistoriaRepository anotacionHistoriaRepository;

    /** Repositorio usado para validar que la historia médica referenciada exista. */
    @Autowired
    private HistoriaMedicaRepository historiaMedicaRepository;

    @Override
    public AnotacionHistoriaRS crear(AnotacionHistoriaRQ request) throws BadRequestException {
        if (request.getHistoriaId() == null || request.getMedicoId() == null) {
            throw new BadRequestException("historiaId y medicoId son obligatorios");
        }
        if (request.getDescripcion() == null || request.getDescripcion().isBlank()) {
            throw new BadRequestException("descripcion es obligatoria");
        }
        if (!historiaMedicaRepository.existsById(Long.valueOf(request.getHistoriaId()))) {
            throw new BadRequestException("No existe la historia médica con id " + request.getHistoriaId());
        }

        AnotacionHistoria anotacion = new AnotacionHistoria();
        anotacion.setHistoriaId(request.getHistoriaId());
        anotacion.setMedicoId(request.getMedicoId());
        anotacion.setDescripcion(request.getDescripcion());
        anotacion.setFecha(LocalDateTime.now());

        anotacion = anotacionHistoriaRepository.save(anotacion);
        return convertirARS(anotacion);
    }

    @Override
    public List<AnotacionHistoriaRS> listarPorRangoFechas(LocalDateTime fechaInicio, LocalDateTime fechaFin) throws BadRequestException {
        if (fechaInicio == null || fechaFin == null) {
            throw new BadRequestException("Debe indicar fechaInicio y fechaFin");
        }
        if (fechaInicio.isAfter(fechaFin)) {
            throw new BadRequestException("fechaInicio no puede ser posterior a fechaFin");
        }
        return anotacionHistoriaRepository.findByFechaBetweenOrderByFechaDesc(fechaInicio, fechaFin)
                .stream()
                .map(this::convertirARS)
                .collect(Collectors.toList());
    }

    /**
     * Valida primero que la historia médica exista, para dar un mensaje
     * claro en vez de simplemente devolver una lista vacía.
     */
    @Override
    public List<AnotacionHistoriaRS> listarPorHistoria(Integer historiaId) throws BadRequestException {
        if (historiaId == null) {
            throw new BadRequestException("historiaId es obligatorio");
        }
        if (!historiaMedicaRepository.existsById(Long.valueOf(historiaId))) {
            throw new BadRequestException("No existe la historia médica con id " + historiaId);
        }
        return anotacionHistoriaRepository.findByHistoriaIdOrderByFechaDesc(historiaId)
                .stream()
                .map(this::convertirARS)
                .collect(Collectors.toList());
    }

    @Override
    public AnotacionHistoriaRS actualizar(AnotacionHistoriaRQ request) throws BadRequestException {
        if (request.getId() == null) {
            throw new BadRequestException("id es obligatorio para actualizar la anotación");
        }
        AnotacionHistoria anotacion = anotacionHistoriaRepository.findById(request.getId())
                .orElseThrow(() -> new BadRequestException("No existe la anotación con id " + request.getId()));

        if (request.getDescripcion() == null || request.getDescripcion().isBlank()) {
            throw new BadRequestException("descripcion es obligatoria");
        }

        anotacion.setDescripcion(request.getDescripcion());
        if (request.getMedicoId() != null) {
            anotacion.setMedicoId(request.getMedicoId());
        }

        anotacion = anotacionHistoriaRepository.save(anotacion);
        return convertirARS(anotacion);
    }

    /**
     * Convierte una entidad (AnotacionHistoria) en su DTO de salida.
     */
    private AnotacionHistoriaRS convertirARS(AnotacionHistoria anotacion) {
        AnotacionHistoriaRS rs = new AnotacionHistoriaRS();
        rs.setId(anotacion.getId());
        rs.setHistoriaId(anotacion.getHistoriaId());
        rs.setMedicoId(anotacion.getMedicoId());
        rs.setFecha(anotacion.getFecha());
        rs.setDescripcion(anotacion.getDescripcion());
        return rs;
    }
}