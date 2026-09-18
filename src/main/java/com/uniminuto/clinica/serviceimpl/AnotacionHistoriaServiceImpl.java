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
 * Incluye validaciones y control de errores para garantizar la integridad de los datos.
 */
@Service
public class AnotacionHistoriaServiceImpl implements AnotacionHistoriaService {

    /**
     * Repositorio inyectado para la persistencia de AnotacionHistoria.
     */
    @Autowired
    private AnotacionHistoriaRepository anotacionRepository;

    /**
     * {@inheritDoc}
     * Implementa blindaje contra datos obligatorios nulos.
     */
    @Override
    @Transactional
    public AnotacionHistoria crearAnotacion(AnotacionHistoria anotacion) throws BadRequestException {
        // Validación 1: El detalle es obligatorio
        if (anotacion.getDetalle() == null || anotacion.getDetalle().trim().isEmpty()) {
            throw new BadRequestException("El detalle de la anotacion es obligatorio.");
        }

        // Validación 2: La historia medica es obligatoria
        if (anotacion.getHistoriaMedica() == null) {
            throw new BadRequestException("La historia medica asociada es obligatoria.");
        }

        // Si pasa todas las validaciones, guardamos en la base de datos
        return anotacionRepository.save(anotacion);
    }

    /**
     * {@inheritDoc}
     * Implementa blindaje contra fechas nulas o incoherentes.
     */
    @Override
    @Transactional(readOnly = true)
    public List<AnotacionHistoria> listarAnotacionesPorFecha(LocalDateTime fechaInicio, LocalDateTime fechaFin) throws BadRequestException {
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
        return anotacionRepository.findByFechaBetweenOrderByFechaDesc(fechaInicio, fechaFin);
    }

    /**
     * {@inheritDoc}
     * Implementa blindaje verificando que la anotacion exista antes de actualizar.
     */
    @Override
    @Transactional
    public AnotacionHistoria actualizarAnotacion(Long id, AnotacionHistoria anotacion) throws BadRequestException {
        // Validación 1: Buscar si la anotacion existe
        AnotacionHistoria anotacionExistente = anotacionRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("La anotacion con ID " + id + " no existe en el sistema."));

        // Validación 2: El detalle es obligatorio
        if (anotacion.getDetalle() == null || anotacion.getDetalle().trim().isEmpty()) {
            throw new BadRequestException("El detalle de la anotacion es obligatorio.");
        }

        // Actualizamos solo los campos que el usuario puede modificar
        anotacionExistente.setDetalle(anotacion.getDetalle());
        anotacionExistente.setFecha(anotacion.getFecha());

        // Guardamos los cambios
        return anotacionRepository.save(anotacionExistente);
    }
}