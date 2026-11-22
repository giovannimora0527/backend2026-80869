package com.uniminuto.clinica.serviceimpl;

import com.uniminuto.clinica.entity.AnotacionHistoria;
import com.uniminuto.clinica.entity.Cita;
import com.uniminuto.clinica.entity.HistoriaMedica;
import com.uniminuto.clinica.entity.Mascota;
import com.uniminuto.clinica.entity.Medico;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.models.AnotacionHistoriaRq;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.repository.AnotacionHistoriaRepository;
import com.uniminuto.clinica.repository.CitaRepository;
import com.uniminuto.clinica.repository.HistoriaMedicaRepository;
import com.uniminuto.clinica.repository.MedicoRepository;
import com.uniminuto.clinica.service.AnotacionHistoriaService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AnotacionHistoriaServiceImpl implements AnotacionHistoriaService {

    private final HistoriaMedicaRepository historiaMedicaRepository;
    private final AnotacionHistoriaRepository anotacionHistoriaRepository;
    private final MedicoRepository medicoRepository;
    private final CitaRepository citaRepository;
    private final EntityManager entityManager;

    public AnotacionHistoriaServiceImpl(
            HistoriaMedicaRepository historiaMedicaRepository,
            AnotacionHistoriaRepository anotacionHistoriaRepository,
            MedicoRepository medicoRepository,
            CitaRepository citaRepository,
            EntityManager entityManager
    ) {
        this.historiaMedicaRepository = historiaMedicaRepository;
        this.anotacionHistoriaRepository = anotacionHistoriaRepository;
        this.medicoRepository = medicoRepository;
        this.citaRepository = citaRepository;
        this.entityManager = entityManager;
    }

    @Override
    @Transactional
    public MiRespuestaRS crearAnotacionHistoria(AnotacionHistoriaRq rq) {
        if (rq == null) {
            throw new BadRequestException("La información de la anotación es obligatoria.");
        }

        if (rq.getDescripcion() == null || rq.getDescripcion().isBlank()) {
            throw new BadRequestException("La descripción de la anotación es obligatoria.");
        }

        if (rq.getMedicoId() == null) {
            throw new BadRequestException("Debes seleccionar un médico.");
        }

        Medico medico = medicoRepository.findById(rq.getMedicoId())
                .orElseThrow(() -> new BadRequestException("El médico indicado no existe."));

        AnotacionHistoria anotacion = new AnotacionHistoria();

        if (rq.getCitaId() != null) {
            if (rq.getDiagnostico() == null || rq.getDiagnostico().isBlank()) {
                throw new BadRequestException("El diagnóstico de la anotación es obligatorio.");
            }

            if (rq.getObservaciones() == null || rq.getObservaciones().isBlank()) {
                throw new BadRequestException("Las observaciones de la anotación son obligatorias.");
            }

            Cita cita = citaRepository.findById(rq.getCitaId())
                    .orElseThrow(() -> new BadRequestException("La cita indicada no existe."));

            Mascota mascota = cita.getMascota();
            if (mascota == null) {
                throw new BadRequestException("La cita no tiene una mascota asociada.");
            }

            if (rq.getMascotaId() != null
                    && !rq.getMascotaId().equals(mascota.getMascotaId())) {
                throw new BadRequestException("La mascota indicada no corresponde a la cita.");
            }

            HistoriaMedica historia = rq.getHistoriaId() == null
                    ? obtenerOCrearHistoria(mascota)
                    : obtenerHistoriaDeMascota(rq.getHistoriaId(), mascota.getMascotaId());

            anotacion.setHistoria(historia);
            anotacion.setCita(cita);
        } else {
            if (rq.getHistoriaId() == null || rq.getMascotaId() == null) {
                throw new BadRequestException("Debes indicar la historia clínica y la mascota.");
            }

            anotacion.setHistoria(
                    obtenerHistoriaDeMascota(rq.getHistoriaId(), rq.getMascotaId())
            );
        }

        anotacion.setMedico(medico);
        anotacion.setDescripcion(rq.getDescripcion().trim());
        anotacion.setDiagnostico(rq.getDiagnostico());
        anotacion.setObservaciones(rq.getObservaciones());
        anotacion.setFecha(LocalDateTime.now());
        anotacionHistoriaRepository.save(anotacion);

        MiRespuestaRS respuesta = new MiRespuestaRS();
        respuesta.setStatus(200);
        respuesta.setMessage("Anotación de historia clínica creada exitosamente.");
        return respuesta;
    }

    private HistoriaMedica obtenerHistoriaDeMascota(Long historiaId, Integer mascotaId) {
        HistoriaMedica historia = historiaMedicaRepository.findById(historiaId)
                .orElseThrow(() -> new BadRequestException(
                        "La historia clínica indicada no existe."
                ));

        if (historia.getMascota() == null
                || !mascotaId.equals(historia.getMascota().getMascotaId())) {
            throw new BadRequestException(
                    "La historia clínica no corresponde a la mascota indicada."
            );
        }

        return historia;
    }

    private HistoriaMedica obtenerOCrearHistoria(Mascota mascota) {
        // Dos solicitudes para la misma mascota deben consultar su historia por turno.
        entityManager.lock(mascota, LockModeType.PESSIMISTIC_WRITE);

        return historiaMedicaRepository.findByMascota(mascota)
                .orElseGet(() -> {
                    HistoriaMedica historia = new HistoriaMedica();
                    historia.setMascota(mascota);
                    historia.setFechaCreacion(LocalDateTime.now());
                    return historiaMedicaRepository.save(historia);
                });
    }

    @Override
    public List<AnotacionHistoria> listarPorHistoria(Long historiaId) {
        if (historiaId == null) {
            throw new BadRequestException("El ID de la historia clínica es obligatorio.");
        }

        HistoriaMedica historia = historiaMedicaRepository.findById(historiaId)
                .orElseThrow(() -> new BadRequestException(
                        "La historia clínica indicada no existe."
                ));

        return anotacionHistoriaRepository.findByHistoriaOrderByFechaDesc(historia);
    }
}