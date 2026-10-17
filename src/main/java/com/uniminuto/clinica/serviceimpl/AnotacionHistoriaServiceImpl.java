
package com.uniminuto.clinica.serviceimpl;

import com.uniminuto.clinica.entity.AnotacionHistoria;
import com.uniminuto.clinica.entity.HistoriaMedica;
import com.uniminuto.clinica.entity.Mascota;
import com.uniminuto.clinica.entity.Medico;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.models.AnotacionHistoriaRq;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.repository.AnotacionHistoriaRepository;
import com.uniminuto.clinica.repository.HistoriaMedicaRepository;
import com.uniminuto.clinica.repository.MascotaRepository;
import com.uniminuto.clinica.repository.MedicoRepository;
import com.uniminuto.clinica.service.AnotacionHistoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AnotacionHistoriaServiceImpl
        implements AnotacionHistoriaService {

    @Autowired
    private HistoriaMedicaRepository historiaMedicaRepository;

    @Autowired
    private AnotacionHistoriaRepository anotacionHistoriaRepository;

    @Autowired
    private MascotaRepository mascotaRepository;

    @Autowired
    private MedicoRepository medicoRepository;

    @Override
    public MiRespuestaRS crearAnotacionHistoria(
            AnotacionHistoriaRq rq) {

        validarAnotacionHistoria(rq);

        Mascota mascota = mascotaRepository.findById(rq.getMascotaId())
                .orElseThrow(() -> new BadRequestException(
                        "La mascota indicada no existe."));

        HistoriaMedica historia = historiaMedicaRepository
                .findById(rq.getHistoriaId())
                .orElseThrow(() -> new BadRequestException(
                        "La historia clínica indicada no existe."));

        if (historia.getMascota() == null
                || !historia.getMascota().getMascotaId()
                .equals(mascota.getMascotaId())) {
            throw new BadRequestException(
                    "La historia clínica no corresponde a la mascota indicada.");
        }

        Medico medico = medicoRepository.findById(rq.getMedicoId())
                .orElseThrow(() -> new BadRequestException(
                        "El médico indicado no existe."));

        AnotacionHistoria anotacion = new AnotacionHistoria();
        anotacion.setHistoria(historia);
        anotacion.setMedico(medico);
        anotacion.setDescripcion(rq.getDescripcion().trim());
        anotacion.setFecha(LocalDateTime.now());

        anotacionHistoriaRepository.save(anotacion);

        MiRespuestaRS respuesta = new MiRespuestaRS();
        respuesta.setStatus(200);
        respuesta.setMessage(
                "Anotación de historia clínica creada exitosamente");

        return respuesta;
    }

    private void validarAnotacionHistoria(
            AnotacionHistoriaRq rq) {

        if (rq == null) {
            throw new BadRequestException(
                    "La información de la anotación es obligatoria.");
        }

        if (rq.getMascotaId() == null) {
            throw new BadRequestException(
                    "El ID de la mascota es obligatorio.");
        }

        if (rq.getHistoriaId() == null) {
            throw new BadRequestException(
                    "El ID de la historia clínica es obligatorio.");
        }

        if (rq.getMedicoId() == null) {
            throw new BadRequestException(
                    "El ID del médico es obligatorio.");
        }

        if (rq.getDescripcion() == null
                || rq.getDescripcion().trim().isEmpty()) {
            throw new BadRequestException(
                    "La descripción de la anotación es obligatoria.");
        }
    }

    @Override
    public java.util.List<AnotacionHistoria> listarPorHistoria(
            Long historiaId) {

        if (historiaId == null) {
            throw new BadRequestException(
                    "El ID de la historia clínica es obligatorio.");
        }

        HistoriaMedica historia = historiaMedicaRepository
                .findById(historiaId)
                .orElseThrow(() -> new BadRequestException(
                        "La historia clínica indicada no existe."));

        return anotacionHistoriaRepository
                .findByHistoriaOrderByFechaDesc(historia);
    }

}
