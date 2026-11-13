package com.uniminuto.clinica.serviceimpl;

import com.uniminuto.clinica.entity.Cita;
import com.uniminuto.clinica.entity.Cliente;
import com.uniminuto.clinica.entity.HorarioMedico;
import com.uniminuto.clinica.entity.Mascota;
import com.uniminuto.clinica.entity.Medico;
import com.uniminuto.clinica.models.CitaRq;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.repository.CitaRepository;
import com.uniminuto.clinica.repository.HorarioMedicoRepository;
import com.uniminuto.clinica.repository.MascotaRepository;
import com.uniminuto.clinica.repository.MedicoRepository;
import com.uniminuto.clinica.service.CitaService;
import com.uniminuto.clinica.exception.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CitaServiceImpl implements CitaService {

    private static final String ESTADO_PROGRAMADA = "PROGRAMADA";
    private static final String ESTADO_CANCELADA = "CANCELADA";
    private static final String ESTADO_CONFIRMADA = "CONFIRMADA";

    /** Duracion estimada de una cita, usada para detectar solapamientos. */
    private static final int DURACION_CITA_MINUTOS = 30;

    @Autowired
    private CitaRepository citaRepository;

    @Autowired
    private MascotaRepository mascotaRepository;

    @Autowired
    private MedicoRepository medicoRepository;

    @Autowired
    private HorarioMedicoRepository horarioRepository;


    @Override
    public List<Cita> listarCitas() throws BadRequestException {
        return citaRepository.findAll();
    }

    @Override
    public List<Cita> listarCitasRango(LocalDateTime fechaInicio, LocalDateTime fechaFin)
            throws BadRequestException {
        this.validarFechas(fechaInicio, fechaFin);
        List<Cita> citas = citaRepository.findByFechaHoraBetween(fechaInicio, fechaFin);
        citas.sort((cita1, cita2)
                -> cita1.getFechaHora().compareTo(cita2.getFechaHora()));
        return citas;
    }

    @Override
    public List<Cita> listarCitasPorMascota(Integer mascotaId) throws BadRequestException {
        if (mascotaId == null || mascotaId <= 0) {
            throw new BadRequestException("El id de la mascota es obligatorio y debe ser mayor a cero.");
        }
        Optional<Mascota> mascotaOptional = mascotaRepository.findById(mascotaId);
        if (mascotaOptional.isEmpty()) {
            throw new BadRequestException("La mascota no existe");
        }
        return citaRepository.findByMascotaOrderByFechaHoraDesc(mascotaOptional.get());
    }

    private void validarFechas(LocalDateTime fechaInicio, LocalDateTime fechaFin) throws BadRequestException {
        if (fechaInicio == null || fechaFin == null) {
            throw new BadRequestException("Las fechas no pueden ser nulas");
        }
        if (fechaInicio.isAfter(fechaFin)) {
            throw new BadRequestException("La fecha de inicio no puede ser mayor a la fecha de fin");
        }
    }

    @Override
    public MiRespuestaRS actualizarCita(CitaRq citaRq) throws BadRequestException {
        this.validarCita(citaRq);
        Optional<Mascota> mascotaOptional = mascotaRepository.findById(citaRq.getMascotaId());
        if (mascotaOptional.isEmpty()) {
            throw new BadRequestException("La mascota no existe");
        }

        Optional<Medico> medicoOptional = medicoRepository.findById(citaRq.getMedicoId());
        if (medicoOptional.isEmpty()) {
            throw new BadRequestException("El médico no existe");
        }

        Optional<Cita> optCita = this.citaRepository.findById(citaRq.getId());
        if (optCita.isEmpty()) {
            throw new BadRequestException("La cita no existe");
        }

        Cita citaActualizar = optCita.get();

        // Regla: no se pueden asignar citas a medicos inactivos (tambien al editar).
        if (!Boolean.TRUE.equals(medicoOptional.get().getEstado())) {
            throw new BadRequestException("No se pueden asignar citas a un medico inactivo.");
        }

        // Si la cita no se esta cancelando, validar solapamiento y horario.
        if (!ESTADO_CANCELADA.equalsIgnoreCase(citaRq.getEstado())) {
            this.validarSolapamiento(medicoOptional.get(), citaRq.getFechaHora(), citaActualizar.getId());
            this.validarHorarioDisponible(medicoOptional.get(), citaRq.getFechaHora());
        }

        citaActualizar.setMedico(medicoOptional.get());
        citaActualizar.setMascota(mascotaOptional.get());
        citaActualizar.setFechaHora(citaRq.getFechaHora());
        citaActualizar.setMotivo(citaRq.getMotivo());
        citaActualizar.setEstado(citaRq.getEstado());
        citaRepository.save(citaActualizar);

        MiRespuestaRS rta = new MiRespuestaRS();
        rta.setMessage("Se agendo una nueva cita para la mascota "
                + mascotaOptional.get().getNombreMascota() + " con el médico " + medicoOptional.get().getNombres() + " "
                + medicoOptional.get().getApellidos() + " para el día " + citaRq.getFechaHora().toString());

        return rta;
    }

    @Override
    public MiRespuestaRS crearCita(CitaRq citaRq) throws BadRequestException {
        this.validarCita(citaRq);
        Optional<Mascota> mascotaOptional = mascotaRepository.findById(citaRq.getMascotaId());
        if (mascotaOptional.isEmpty()) {
            throw new BadRequestException("La mascota no existe");
        }

        Optional<Medico> medicoOptional = medicoRepository.findById(citaRq.getMedicoId());
        if (medicoOptional.isEmpty()) {
            throw new BadRequestException("El médico no existe");
        }

        // Regla de negocio: no se pueden asignar citas a medicos inactivos.
        if (!Boolean.TRUE.equals(medicoOptional.get().getEstado())) {
            throw new BadRequestException("No se pueden asignar citas a un medico inactivo.");
        }

        // Regla: el medico no puede tener dos citas que se solapen (ventana de
        // 30 minutos, ignorando las citas canceladas).
        this.validarSolapamiento(medicoOptional.get(), citaRq.getFechaHora(), null);

        // Regla: la cita debe caer dentro del horario de disponibilidad del medico.
        this.validarHorarioDisponible(medicoOptional.get(), citaRq.getFechaHora());

        Mascota mascota = mascotaOptional.get();
        Medico medico = medicoOptional.get();
        Cita nuevaCita = new Cita();
        nuevaCita.setFechaHora(citaRq.getFechaHora());
        nuevaCita.setCliente(mascota.getCliente());
        nuevaCita.setMascota(mascota);
        nuevaCita.setMedico(medico);
        nuevaCita.setMotivo(citaRq.getMotivo());
        nuevaCita.setEstado(ESTADO_PROGRAMADA);
        citaRepository.save(nuevaCita);

        MiRespuestaRS rta = new MiRespuestaRS();
        rta.setMessage("Se agendo una nueva cita para la mascota "
                + mascota.getNombreMascota() + " con el médico " + medico.getNombres() + " "
                + medico.getApellidos() + " para el día " + citaRq.getFechaHora().toString());

        return rta;
    }

    /**
     * Valida que el medico no tenga otra cita que se solape con la fecha/hora
     * indicada (ventana de +/- la duracion de la cita). Ignora las citas
     * canceladas y, al editar, excluye la propia cita (citaIdExcluir).
     */
    private void validarSolapamiento(Medico medico, LocalDateTime fechaHora, Long citaIdExcluir) {
        LocalDateTime desde = fechaHora.minusMinutes(DURACION_CITA_MINUTOS);
        LocalDateTime hasta = fechaHora.plusMinutes(DURACION_CITA_MINUTOS);
        List<Cita> solapadas = citaRepository.findByMedicoAndFechaHoraBetween(medico, desde, hasta);
        boolean hayConflicto = solapadas.stream().anyMatch(c ->
                (citaIdExcluir == null || !c.getId().equals(citaIdExcluir))
                        && !ESTADO_CANCELADA.equalsIgnoreCase(c.getEstado()));
        if (hayConflicto) {
            throw new BadRequestException(
                    "El medico ya tiene una cita en ese rango de horario.");
        }
    }

    /**
     * Valida que la fecha/hora de la cita caiga dentro del horario de
     * disponibilidad del medico. Si el medico no tiene horarios definidos, se
     * asume disponible (no se restringe).
     */
    private void validarHorarioDisponible(Medico medico, LocalDateTime fechaHora) {
        List<HorarioMedico> horarios = horarioRepository.findByMedicoIdAndActivoTrue(medico.getId());
        if (horarios.isEmpty()) {
            return;
        }
        int dia = fechaHora.getDayOfWeek().getValue();
        boolean disponible = horarios.stream().anyMatch(h ->
                h.getDiaSemana() == dia
                        && !fechaHora.toLocalTime().isBefore(h.getHoraInicio())
                        && !fechaHora.toLocalTime().isAfter(h.getHoraFin()));
        if (!disponible) {
            throw new BadRequestException(
                    "El medico no atiende en el horario seleccionado.");
        }
    }

    private void validarCita(CitaRq citaRq) throws BadRequestException {
        if (citaRq == null) {
            throw new BadRequestException("La cita no puede ser nula");
        }
        if (citaRq.getFechaHora() == null) {
            throw new BadRequestException("La fecha y hora de la cita no puede ser nula");
        }
        if (citaRq.getFechaHora().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("La fecha y hora de la cita no puede ser anterior a la fecha y hora actual");
        }
        if (citaRq.getMascotaId() == null) {
            throw new BadRequestException("El id de la mascota no puede ser nulo");
        }
        if (citaRq.getMedicoId() == null) {
            throw new BadRequestException("El id del medico no puede ser nulo");
        }
        if (citaRq.getMotivo() == null || citaRq.getMotivo().isEmpty()) {
            throw new BadRequestException("El motivo de la cita no puede ser nulo o vacio");
        }
    }
}
