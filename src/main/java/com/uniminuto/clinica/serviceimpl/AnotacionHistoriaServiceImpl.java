package com.uniminuto.clinica.serviceimpl;

import com.uniminuto.clinica.entity.AnotacionHistoria;
import com.uniminuto.clinica.entity.HistoriaMedica;
import com.uniminuto.clinica.entity.Medico;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.exception.ResourceNotFoundException;
import com.uniminuto.clinica.models.AnotacionHistoriaRq;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.repository.AnotacionHistoriaRepository;
import com.uniminuto.clinica.repository.HistoriaMedicaRepository;
import com.uniminuto.clinica.repository.MedicoRepository;
import com.uniminuto.clinica.service.AnotacionHistoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Implementación de la lógica de negocio para gestionar las anotaciones de las historias clínicas.
 * Encargada de validar la existencia de historias previas y médicos registrados antes de guardar anotaciones.
 *
 * @author Yulian Estefanía Montealegre
 * @version 1.0
 */
@Service
public class AnotacionHistoriaServiceImpl implements AnotacionHistoriaService {

    @Autowired
    private HistoriaMedicaRepository historiaMedicaRepository;

    @Autowired
    private AnotacionHistoriaRepository anotacionHistoriaRepository;

    @Autowired
    private MedicoRepository medicoRepository;

    /**
     * Registra una anotación médica vinculándola a una historia clínica existente.
     * La historia clínica debe existir previamente en el sistema (retorna 404 en caso contrario).
     *
     * @param anotacionHistoriaRq DTO con los datos de la anotación a registrar.
     * @return MiRespuestaRS Objeto de respuesta estandarizado.
     */
    @Override
    public MiRespuestaRS crearAnotacionHistoria(AnotacionHistoriaRq anotacionHistoriaRq) {
        this.validarAnotacionHistoria(anotacionHistoriaRq);

        // Validar que la historia médica exista en la base de datos (404 Not Found)
        HistoriaMedica historia = historiaMedicaRepository.findById(anotacionHistoriaRq.getHistoriaId())
                .orElseThrow(() -> new ResourceNotFoundException("La historia médica con ID " + anotacionHistoriaRq.getHistoriaId() + " no existe."));

        // Validar que el médico veterinario exista (404 Not Found)
        Medico medico = medicoRepository.findById(anotacionHistoriaRq.getMedicoId())
                .orElseThrow(() -> new ResourceNotFoundException("El médico veterinario con ID " + anotacionHistoriaRq.getMedicoId() + " no existe."));

        // Mapeo y creación de la entidad AnotacionHistoria
        AnotacionHistoria nuevaAnotacion = new AnotacionHistoria();
        nuevaAnotacion.setHistoria(historia);
        nuevaAnotacion.setMedico(medico);
        nuevaAnotacion.setDescripcion(anotacionHistoriaRq.getDescripcion());

        // Asignación de fecha/hora actual de la anotación
        nuevaAnotacion.setFecha(LocalDateTime.now());

        // Guardar en la base de datos
        anotacionHistoriaRepository.save(nuevaAnotacion);

        // Construir la respuesta estandarizada evitando referencias circulares masivas en JSON
        MiRespuestaRS response = new MiRespuestaRS();
        response.setStatus(200);
        response.setMessage("Anotación de historia clínica registrada exitosamente.");
        response.setData(null);
        return response;
    }

    /**
     * Valida que los campos obligatorios del DTO de anotación no sean nulos ni vacíos.
     *
     * @param anotacionHistoriaRq DTO a validar.
     */
    private void validarAnotacionHistoria(AnotacionHistoriaRq anotacionHistoriaRq) {
        if (anotacionHistoriaRq == null) {
            throw new BadRequestException("Los datos de la anotación de historia clínica no pueden ser nulos.");
        }
        if (anotacionHistoriaRq.getHistoriaId() == null) {
            throw new BadRequestException("El ID de la historia clínica es obligatorio.");
        }
        if (anotacionHistoriaRq.getMedicoId() == null) {
            throw new BadRequestException("El ID del médico es obligatorio.");
        }
        if (anotacionHistoriaRq.getDescripcion() == null || anotacionHistoriaRq.getDescripcion().trim().isEmpty()) {
            throw new BadRequestException("La descripción es obligatoria.");
        }
    }

    /**
     * Consulta y retorna todas las anotaciones asociadas a una historia clínica específica,
     * ordenadas cronológicamente de forma descendente.
     *
     * @param historiaId ID de la historia clínica.
     * @return List<AnotacionHistoria> Lista de anotaciones encontradas.
     */
    @Override
    public List<AnotacionHistoria> listarAnotacionesPorHistoria(Long historiaId) {
        HistoriaMedica historia = historiaMedicaRepository.findById(historiaId)
                .orElseThrow(() -> new ResourceNotFoundException("La historia clínica con ID " + historiaId + " no existe."));

        return anotacionHistoriaRepository.findByHistoriaOrderByFechaDesc(historia);
    }
}