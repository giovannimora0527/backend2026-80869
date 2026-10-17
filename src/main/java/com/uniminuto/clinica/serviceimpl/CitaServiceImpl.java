
package com.uniminuto.clinica.serviceimpl;

import com.uniminuto.clinica.entity.Cita;
import com.uniminuto.clinica.entity.Mascota;
import com.uniminuto.clinica.entity.Medico;
import com.uniminuto.clinica.models.CitaRq;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.repository.CitaRepository;
import com.uniminuto.clinica.repository.MascotaRepository;
import com.uniminuto.clinica.repository.MedicoRepository;
import com.uniminuto.clinica.service.CitaService;
import com.uniminuto.clinica.exception.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
public class CitaServiceImpl implements CitaService {

    private static final String ESTADO_PROGRAMADA = "PROGRAMADA";
    private static final String ESTADO_PENDIENTE = "PENDIENTE";
    private static final String ESTADO_CONFIRMADA = "CONFIRMADA";
    private static final String ESTADO_ATENDIDA = "ATENDIDA";
    private static final String ESTADO_CANCELADA = "CANCELADA";
    private static final int MARGEN_MINUTOS = 15;

    @Autowired
    private CitaRepository citaRepository;

    @Autowired
    private MascotaRepository mascotaRepository;

    @Autowired
    private MedicoRepository medicoRepository;

    @Override
    public List<Cita> listarCitas() throws BadRequestException {
        return citaRepository.findAll().stream()
                .sorted(Comparator.comparing(
                        Cita::getFechaHora,
                        Comparator.nullsLast(Comparator.reverseOrder())
                ))
                .toList();
    }

    @Override
    public List<Cita> listarCitasRango(
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin
    ) throws BadRequestException {

        validarFechas(fechaInicio, fechaFin);

        return citaRepository
                .findByFechaHoraBetween(fechaInicio, fechaFin)
                .stream()
                .sorted(Comparator.comparing(
                        Cita::getFechaHora,
                        Comparator.nullsLast(Comparator.reverseOrder())
                ))
                .toList();
    }

    private void validarFechas(
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin
    ) throws BadRequestException {

        if (fechaInicio == null || fechaFin == null) {
            throw new BadRequestException(
                    "Las fechas de inicio y fin son obligatorias."
            );
        }

        if (fechaInicio.isAfter(fechaFin)) {
            throw new BadRequestException(
                    "La fecha de inicio no puede ser posterior a la fecha de fin."
            );
        }
    }

    @Override
    public MiRespuestaRS crearCita(CitaRq citaRq)
            throws BadRequestException {

        validarCita(citaRq);

        Mascota mascota = mascotaRepository
                .findById(citaRq.getMascotaId())
                .orElseThrow(() ->
                        new BadRequestException("La mascota no existe.")
                );

        Medico medico = medicoRepository
                .findById(citaRq.getMedicoId())
                .orElseThrow(() ->
                        new BadRequestException("El médico no existe.")
                );

        validarHorario(mascota, medico, citaRq.getFechaHora(), null);

        Cita nuevaCita = new Cita();
        nuevaCita.setFechaHora(citaRq.getFechaHora());
        nuevaCita.setCliente(mascota.getCliente());
        nuevaCita.setMascota(mascota);
        nuevaCita.setMedico(medico);
        nuevaCita.setMotivo(citaRq.getMotivo().trim());
        nuevaCita.setEstado(ESTADO_PROGRAMADA);

        citaRepository.save(nuevaCita);

        MiRespuestaRS respuesta = new MiRespuestaRS();
        respuesta.setMessage(
                "Cita programada para " + mascota.getNombreMascota()
                        + " con el médico " + medico.getNombres()
                        + " " + medico.getApellidos()
                        + " el " + citaRq.getFechaHora()
        );

        return respuesta;
    }

    @Override
    public MiRespuestaRS actualizarCita(CitaRq citaRq)
            throws BadRequestException {

        validarCita(citaRq);

        if (citaRq.getId() == null) {
            throw new BadRequestException(
                    "El ID de la cita es obligatorio para actualizarla."
            );
        }

        Cita citaActualizar = citaRepository
                .findById(citaRq.getId())
                .orElseThrow(() ->
                        new BadRequestException("La cita no existe.")
                );

        String estadoActual = normalizarEstado(citaActualizar.getEstado());

        if (ESTADO_CANCELADA.equals(estadoActual)) {
            throw new BadRequestException(
                    "Una cita cancelada no se puede editar. "
                            + "Debe existir una operación explícita para reactivarla."
            );
        }

        if (ESTADO_ATENDIDA.equals(estadoActual)) {
            throw new BadRequestException(
                    "Una cita atendida no permite modificaciones generales."
            );
        }

        Mascota mascota = mascotaRepository
                .findById(citaRq.getMascotaId())
                .orElseThrow(() ->
                        new BadRequestException("La mascota no existe.")
                );

        Medico medico = medicoRepository
                .findById(citaRq.getMedicoId())
                .orElseThrow(() ->
                        new BadRequestException("El médico no existe.")
                );

        validarHorario(
                mascota,
                medico,
                citaRq.getFechaHora(),
                citaActualizar.getId()
        );

        citaActualizar.setMascota(mascota);
        citaActualizar.setCliente(mascota.getCliente());
        citaActualizar.setMedico(medico);
        citaActualizar.setFechaHora(citaRq.getFechaHora());
        citaActualizar.setMotivo(citaRq.getMotivo().trim());

        // En esta operación no se permite cambiar el estado libremente.
        citaRepository.save(citaActualizar);

        MiRespuestaRS respuesta = new MiRespuestaRS();
        respuesta.setMessage(
                "La cita " + citaActualizar.getId()
                        + " se actualizó correctamente."
        );

        return respuesta;
    }

    private void validarHorario(
            Mascota mascota,
            Medico medico,
            LocalDateTime fechaHora,
            Long idCitaExcluir
    ) throws BadRequestException {

        if (!fechaHora.isAfter(LocalDateTime.now())) {
            throw new BadRequestException(
                    "No se pueden programar citas en el pasado."
            );
        }

        LocalDateTime inicio = fechaHora.minusMinutes(MARGEN_MINUTOS);
        LocalDateTime fin = fechaHora.plusMinutes(MARGEN_MINUTOS);

        List<Cita> citasMascota = idCitaExcluir == null
                ? citaRepository.findByMascotaAndFechaHoraBetween(
                mascota, inicio, fin
        )
                : citaRepository.findByMascotaAndFechaHoraBetweenAndIdNot(
                mascota, inicio, fin, idCitaExcluir
        );

        boolean conflictoMascota = citasMascota.stream()
                .anyMatch(cita -> esEstadoActivo(cita.getEstado()));

        if (conflictoMascota) {
            throw new BadRequestException(
                    "La mascota ya tiene una cita en ese horario."
            );
        }

        List<Cita> citasMedico = idCitaExcluir == null
                ? citaRepository.findByMedicoAndFechaHoraBetween(
                medico, inicio, fin
        )
                : citaRepository.findByMedicoAndFechaHoraBetweenAndIdNot(
                medico, inicio, fin, idCitaExcluir
        );

        boolean conflictoMedico = citasMedico.stream()
                .anyMatch(cita -> esEstadoActivo(cita.getEstado()));

        if (conflictoMedico) {
            throw new BadRequestException(
                    "El médico ya tiene una cita en ese horario."
            );
        }
    }

    private boolean esEstadoActivo(String estado) {
        String estadoNormalizado = normalizarEstado(estado);

        return ESTADO_PROGRAMADA.equals(estadoNormalizado)
                || ESTADO_PENDIENTE.equals(estadoNormalizado)
                || ESTADO_CONFIRMADA.equals(estadoNormalizado);
    }

    private String normalizarEstado(String estado) {
        if (estado == null) {
            return "";
        }

        return estado.trim().toUpperCase(Locale.ROOT);
    }

    private void validarCita(CitaRq citaRq)
            throws BadRequestException {

        if (citaRq == null) {
            throw new BadRequestException(
                    "Los datos de la cita son obligatorios."
            );
        }

        if (citaRq.getFechaHora() == null) {
            throw new BadRequestException(
                    "La fecha y hora de la cita son obligatorias."
            );
        }

        if (citaRq.getMascotaId() == null) {
            throw new BadRequestException(
                    "Debes seleccionar una mascota."
            );
        }

        if (citaRq.getMedicoId() == null) {
            throw new BadRequestException(
                    "Debes seleccionar un médico."
            );
        }

        if (citaRq.getMotivo() == null
                || citaRq.getMotivo().isBlank()) {
            throw new BadRequestException(
                    "El motivo de la cita es obligatorio."
            );
        }
    }
}
