package main.java.com.uniminuto.clinica.serviceimpl;

import com.uniminuto.clinica.models.Cita;
import com.uniminuto.clinica.repository.CitaRepository;
import com.uniminuto.clinica.service.CitaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * Implementación de la lógica de negocio para Citas Médicas.
 */
@Service
public class CitaServiceImpl implements CitaService {

    @Autowired
    private CitaRepository citaRepository;

    private static final List<String> ESTADOS_ACTIVOS = List.of("AGENDADA", "APROBADA");

    @Override
    public Cita crearCita(Cita cita) {
        // Asignación de estado por defecto si no es provisto
        if (cita.getEstado() == null || cita.getEstado().isBlank()) {
            cita.setEstado("AGENDADA");
        }

        // Regla 1: Validar que el médico esté disponible en esa fecha y hora
        if (cita.getMedico() != null && cita.getMedico().getId() != null) {
            boolean medicoOcupado = citaRepository.existsByMedicoIdAndFechaHoraAndEstadoIn(
                    cita.getMedico().getId(), cita.getFechaHora(), ESTADOS_ACTIVOS);
            if (medicoOcupado) {
                throw new IllegalArgumentException("El médico seleccionado no se encuentra disponible en este horario.");
            }
        }

        // Regla 2: Evitar la creación de citas duplicadas para un mismo paciente en el mismo horario
        boolean pacienteOcupado = citaRepository.existsByPacienteAndFechaHoraAndEstadoIn(
                cita.getPaciente(), cita.getFechaHora(), ESTADOS_ACTIVOS);
        if (pacienteOcupado) {
            throw new IllegalArgumentException("El paciente ya cuenta con una cita agendada o aprobada en este horario.");
        }

        return citaRepository.save(cita);
    }

    @Override
    public Cita actualizarCita(Long id, Cita citaActualizada) {
        Cita citaExistente = citaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró la cita con ID: " + id));

        // Regla 3: Validar que la cita se encuentre en un estado modificable (AGENDADA o APROBADA)
        if (!ESTADOS_ACTIVOS.contains(citaExistente.getEstado())) {
            throw new IllegalArgumentException("Únicamente se pueden actualizar citas en estado AGENDADA o APROBADA.");
        }

        citaExistente.setFechaHora(citaActualizada.getFechaHora());
        citaExistente.setMotivo(citaActualizada.getMotivo());
        citaExistente.setPaciente(citaActualizada.getPaciente());
        if (citaActualizada.getEstado() != null && !citaActualizada.getEstado().isBlank()) {
            citaExistente.setEstado(citaActualizada.getEstado());
        }

        return citaRepository.save(citaExistente);
    }

    @Override
    public List<Cita> filtrarCitas(LocalDate inicio, LocalDate fin) {
        LocalDateTime fechaInicio = inicio.atStartOfDay();
        LocalDateTime fechaFin = fin.atTime(LocalTime.MAX);
        return citaRepository.findByFechaHoraBetweenOrderByFechaHoraDesc(fechaInicio, fechaFin);
    }
}
