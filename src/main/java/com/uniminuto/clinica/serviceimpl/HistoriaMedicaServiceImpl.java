
package com.uniminuto.clinica.serviceimpl;

import com.uniminuto.clinica.entity.HistoriaMedica;
import com.uniminuto.clinica.entity.Mascota;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.models.HistoriaMedicaRq;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.repository.HistoriaMedicaRepository;
import com.uniminuto.clinica.repository.MascotaRepository;
import com.uniminuto.clinica.service.HistoriaMedicaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class HistoriaMedicaServiceImpl implements HistoriaMedicaService {

    @Autowired
    private HistoriaMedicaRepository historiaMedicaRepository;

    @Autowired
    private MascotaRepository mascotaRepository;

    @Override
    public MiRespuestaRS crearHistoriaMedica(HistoriaMedicaRq rq) {
        if (rq == null || rq.getMascotaId() == null) {
            throw new BadRequestException("El ID de la mascota es obligatorio.");
        }

        Mascota mascota = mascotaRepository.findById(rq.getMascotaId())
                .orElseThrow(() ->
                        new BadRequestException("La mascota indicada no existe."));

        if (historiaMedicaRepository.findByMascota(mascota).isPresent()) {
            throw new BadRequestException(
                    "La mascota ya tiene una historia clínica registrada.");
        }

        HistoriaMedica historia = new HistoriaMedica();
        historia.setMascota(mascota);
        historia.setFechaCreacion(LocalDateTime.now());

        historiaMedicaRepository.save(historia);

        MiRespuestaRS respuesta = new MiRespuestaRS();
        respuesta.setStatus(200);
        respuesta.setMessage("Historia clínica creada exitosamente.");

        return respuesta;
    }

    @Override
    public List<HistoriaMedica> listarHistoriasMedicas() {
        return historiaMedicaRepository.findAll();
    }
}
