package com.uniminuto.clinica.serviceimpl;

import com.uniminuto.clinica.entity.AnotacionHistoria;
import com.uniminuto.clinica.entity.HistoriaMedica;
import com.uniminuto.clinica.entity.Mascota;
import com.uniminuto.clinica.entity.Medico;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.models.AnotacionHistoriaRequest;
import com.uniminuto.clinica.models.HistoriaConAnotacionesRS;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.repository.AnotacionHistoriaRepository;
import com.uniminuto.clinica.repository.HistoriaMedicaRepository;
import com.uniminuto.clinica.repository.MascotaRepository;
import com.uniminuto.clinica.repository.MedicoRepository;
import com.uniminuto.clinica.service.HistoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementacion del servicio de historias medicas.
 */
@Service
public class HistoriaServiceImpl implements HistoriaService {

    @Autowired
    private HistoriaMedicaRepository historiaMedicaRepository;

    @Autowired
    private AnotacionHistoriaRepository anotacionHistoriaRepository;

    @Autowired
    private MascotaRepository mascotaRepository;

    @Autowired
    private MedicoRepository medicoRepository;

    @Override
    public AnotacionHistoria crearAnotacion(AnotacionHistoriaRequest request) throws BadRequestException {
        if (request.getMascotaId() == null || request.getMedicoId() == null || request.getDescripcion() == null || request.getDescripcion().isEmpty()) {
            throw new BadRequestException("All fields (mascotaId, medicoId, descripcion) must contain information");
        }

        Mascota mascota = mascotaRepository.findById(request.getMascotaId())
                .orElseThrow(() -> new BadRequestException("Not exist a pet with id " + request.getMascotaId()));

        Medico medico = medicoRepository.findById(request.getMedicoId())
                .orElseThrow(() -> new BadRequestException("Not exist a doctor with id " + request.getMedicoId()));

        HistoriaMedica historia = historiaMedicaRepository.findByPaciente(mascota).orElse(null);

        if (historia == null) {
            historia = new HistoriaMedica();
            historia.setPaciente(mascota);
            historia.setFechaCreacion(LocalDateTime.now());
            historia = historiaMedicaRepository.save(historia);
        }

        AnotacionHistoria anotacion = new AnotacionHistoria();
        anotacion.setHistoriaMedica(historia);
        anotacion.setMedico(medico);
        anotacion.setFecha(LocalDateTime.now());
        anotacion.setDescripcion(request.getDescripcion());

        return anotacionHistoriaRepository.save(anotacion);
    }

    @Override
    public AnotacionHistoria actualizarAnotacion(Integer id, AnotacionHistoriaRequest request) throws BadRequestException {
        AnotacionHistoria anotacionExistente = anotacionHistoriaRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("The history does not exist"));

        if (request.getDescripcion() == null || request.getDescripcion().isEmpty()) {
            throw new BadRequestException("The description must contain information");
        }

        anotacionExistente.setDescripcion(request.getDescripcion());
        anotacionExistente.setFecha(LocalDateTime.now());

        if (request.getMedicoId() != null) {
            Medico medico = medicoRepository.findById(request.getMedicoId())
                    .orElseThrow(() -> new BadRequestException("Not exist a doctor with id " + request.getMedicoId()));
            anotacionExistente.setMedico(medico);
        }

        return anotacionHistoriaRepository.save(anotacionExistente);
    }

    @Override
    public List<HistoriaConAnotacionesRS> listarHistorias(Integer historiaId, LocalDateTime fechaInicial, LocalDateTime fechaFinal) throws BadRequestException {
        if ((fechaInicial != null && fechaFinal == null) || (fechaInicial == null && fechaFinal != null)) {
            throw new BadRequestException("Both start and end dates must be provided, or neither");
        }
        if (fechaInicial != null && fechaFinal != null && fechaInicial.isAfter(fechaFinal)) {
            throw new BadRequestException("The end date cannot be later than the start date");
        }

        List<HistoriaMedica> historiasList = new ArrayList<>();

        if (historiaId != null) {
            HistoriaMedica historia = historiaMedicaRepository.findById(historiaId)
                    .orElseThrow(() -> new BadRequestException("Not exist a history with id " + historiaId));
            historiasList.add(historia);
        } else {
            historiasList = historiaMedicaRepository.findAll();
        }

        List<HistoriaConAnotacionesRS> responseList = new ArrayList<>();

        for (HistoriaMedica historia : historiasList) {
            List<AnotacionHistoria> anotaciones;
            if (fechaInicial != null && fechaFinal != null) {
                anotaciones = anotacionHistoriaRepository.findByHistoriaMedicaAndFechaBetweenOrderByFechaDesc(historia, fechaInicial, fechaFinal);
            } else {
                anotaciones = anotacionHistoriaRepository.findByHistoriaMedicaOrderByFechaDesc(historia);
            }

            HistoriaConAnotacionesRS responseItem = new HistoriaConAnotacionesRS();
            responseItem.setHistoriaId(historia.getId());
            responseItem.setMascotaId(historia.getPaciente() != null ? historia.getPaciente().getMascotaId() : null);
            responseItem.setFechaCreacionHistoria(historia.getFechaCreacion());
            responseItem.setAnotaciones(anotaciones);

            responseList.add(responseItem);
        }

        return responseList;
    }

    @Override
    public MiRespuestaRS eliminarAnotacion(Integer id) throws BadRequestException {
        AnotacionHistoria anotacionExistente = anotacionHistoriaRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("The history does not exist"));

        anotacionHistoriaRepository.delete(anotacionExistente);

        MiRespuestaRS response = new MiRespuestaRS();
        response.setStatus(200);
        response.setMessage("Annotation deleted successfully");
        return response;
    }

    @Override
    public MiRespuestaRS eliminarHistoria(Integer id) throws BadRequestException {
        HistoriaMedica historiaExistente = historiaMedicaRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("The history does not exist"));

        historiaMedicaRepository.delete(historiaExistente);

        MiRespuestaRS response = new MiRespuestaRS();
        response.setStatus(200);
        response.setMessage("History deleted successfully");
        return response;
    }
}
