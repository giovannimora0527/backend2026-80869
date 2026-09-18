package com.uniminuto.clinica.serviceimpl;

import com.uniminuto.clinica.entity.Cita;
import com.uniminuto.clinica.entity.Cliente;
import com.uniminuto.clinica.entity.Mascota;
import com.uniminuto.clinica.entity.Medico;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.models.CitaRequest;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.repository.CitaRepository;
import com.uniminuto.clinica.repository.ClienteRepository;
import com.uniminuto.clinica.repository.MascotaRepository;
import com.uniminuto.clinica.repository.MedicoRepository;
import com.uniminuto.clinica.service.CitaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CitaServiceImpl implements CitaService {

    @Autowired
    private CitaRepository citaRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private MascotaRepository mascotaRepository;

    @Autowired
    private MedicoRepository medicoRepository;


    @Override
    public List<Cita> listarCitas() throws BadRequestException {
        return citaRepository.findAll();
    }

    @Override
    public List<Cita> filtrarCitas(LocalDateTime fechaInicial, LocalDateTime fechaFinal)
            throws BadRequestException {
        if (fechaInicial != null && fechaFinal != null && fechaInicial.isAfter(fechaFinal)) {
            throw new BadRequestException("The end date cannot be later than the start date");
        }
        return citaRepository.findByFechaHoraBetweenOrderByFechaHoraDesc(fechaInicial, fechaFinal);
    }

    @Override
    public Cita crearCita(CitaRequest citaRequest) throws BadRequestException {

        if ((citaRequest.getMotivo() == null || citaRequest.getMotivo().isEmpty()) || (citaRequest.getEstado() == null || citaRequest.getEstado().isEmpty()))
            throw new BadRequestException("The motive and state must contain information");

        if (citaRequest.getFechaHora() == null)
            throw new BadRequestException("The date cannot be empty");

        Cliente cliente = clienteRepository.findById(citaRequest.getClienteId())
                .orElseThrow(() -> new BadRequestException("Not exist a client with id " + citaRequest.getClienteId()));

        Mascota mascota = mascotaRepository.findById(citaRequest.getMascotaId())
                .orElseThrow(() -> new BadRequestException("Not exist a pet with id " + citaRequest.getMascotaId()));

        Medico medico = medicoRepository.findById(citaRequest.getMedicoId())
                .orElseThrow(() -> new BadRequestException("Not exist a doctor with id " + citaRequest.getMedicoId()));

        Cita cita = new Cita();
        cita.setCliente(cliente);
        cita.setMascota(mascota);
        cita.setMedico(medico);
        cita.setFechaHora(citaRequest.getFechaHora());
        cita.setEstado(citaRequest.getEstado());
        cita.setMotivo(citaRequest.getMotivo());

        return citaRepository.save(cita);
    }

    @Override
    public Cita actualizarCita(Integer id, CitaRequest citaRequest) throws BadRequestException {

        if ((citaRequest.getMotivo() == null || citaRequest.getMotivo().isEmpty()) || (citaRequest.getEstado() == null || citaRequest.getEstado().isEmpty()))
            throw new BadRequestException("The motive and state must contain information");

        if (citaRequest.getFechaHora() == null)
            throw new BadRequestException("The date cannot be empty");

        Cita citaExistente = citaRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Not exist a appointment with id " + id));

        Cliente cliente = clienteRepository.findById(citaRequest.getClienteId())
                .orElseThrow(() -> new BadRequestException("Not exist a client with id " + citaRequest.getClienteId()));

        Mascota mascota = mascotaRepository.findById(citaRequest.getMascotaId())
                .orElseThrow(() -> new BadRequestException("Not exist a pet with id " + citaRequest.getMascotaId()));

        Medico medico = medicoRepository.findById(citaRequest.getMedicoId())
                .orElseThrow(() -> new BadRequestException("Not exist a doctor with id " + citaRequest.getMedicoId()));

        citaExistente.setCliente(cliente);
        citaExistente.setMascota(mascota);
        citaExistente.setMedico(medico);
        citaExistente.setFechaHora(citaRequest.getFechaHora());
        citaExistente.setEstado(citaRequest.getEstado());
        citaExistente.setMotivo(citaRequest.getMotivo());

        return citaRepository.save(citaExistente);
    }

    @Override
    public MiRespuestaRS eliminarCita(Integer id) throws BadRequestException {
        Cita citaExistente = citaRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Not exist a appointment with id " + id));

        citaRepository.delete(citaExistente);

        MiRespuestaRS response = new MiRespuestaRS();
        response.setStatus(200);
        response.setMessage("Appointment cancelled successfully");

        return response;
    }
}
