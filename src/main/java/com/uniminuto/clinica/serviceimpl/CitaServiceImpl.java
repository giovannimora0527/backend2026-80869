package com.uniminuto.clinica.serviceimpl;

import com.uniminuto.clinica.entity.Cita;
import com.uniminuto.clinica.entity.Cliente;
import com.uniminuto.clinica.entity.Mascota;
import com.uniminuto.clinica.entity.Medico;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.exception.ConflictException;
import com.uniminuto.clinica.exception.ResourceNotFoundException;
import com.uniminuto.clinica.models.CitaRq;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.repository.CitaRepository;
import com.uniminuto.clinica.repository.ClienteRepository;
import com.uniminuto.clinica.repository.MascotaRepository;
import com.uniminuto.clinica.repository.MedicoRepository;
import com.uniminuto.clinica.service.CitaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Implementación de la lógica de negocio para la gestión de citas veterinarias.
 * Valida disponibilidad de médicos, mascotas, reglas de horario, cliente y transiciones de estado.
 *
 * @author Yulian Estefanía Montealegre
 * @version 2.0
 */
@Service
public class CitaServiceImpl implements CitaService {

    private static final String ESTADO_CANCELADA = "CANCELADA";
    private static final String ESTADO_ATENDIDA = "ATENDIDA";

    /** Estados con los que puede crearse una nueva cita. */
    private static final Set<String> ESTADOS_INICIALES = Set.of("PENDIENTE", "PROGRAMADA", "CONFIRMADA");

    /** Estados en los que se permite modificar los datos de la cita. */
    private static final Set<String> ESTADOS_EDITABLES = Set.of("PENDIENTE", "PROGRAMADA", "CONFIRMADA");

    /** Transiciones de estado permitidas en el flujo de citas. */
    private static final Map<String, Set<String>> TRANSICIONES = Map.of(
            "PENDIENTE", Set.of("CONFIRMADA", "PROGRAMADA", "CANCELADA"),
            "PROGRAMADA", Set.of("CONFIRMADA", "ATENDIDA", "CANCELADA"),
            "CONFIRMADA", Set.of("ATENDIDA", "CANCELADA"),
            "ATENDIDA", Set.of(),
            "CANCELADA", Set.of()
    );

    @Autowired
    private CitaRepository citaRepository;

    @Autowired
    private MascotaRepository mascotaRepository;

    @Autowired
    private MedicoRepository medicoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    /**
     * Valida que ni el médico ni la mascota tengan otra cita activa en una ventana de ±15 minutos.
     *
     * @param medico     Médico asignado.
     * @param mascota    Mascota paciente.
     * @param fechaHora  Fecha y hora propuesta para la cita.
     * @param excluirId  ID de la cita que se edita (-1L al crear) para no chocar consigo misma.
     */
    private void validarDisponibilidad(Medico medico, Mascota mascota, LocalDateTime fechaHora, Long excluirId) {
        LocalDateTime ini = fechaHora.minusMinutes(15);
        LocalDateTime fin = fechaHora.plusMinutes(15);

        boolean choqueMedico = citaRepository.findByMedicoAndFechaHoraBetween(medico, ini, fin).stream()
                .anyMatch(c -> !c.getId().equals(excluirId) && !ESTADO_CANCELADA.equalsIgnoreCase(c.getEstado()));
        if (choqueMedico) {
            throw new ConflictException("El veterinario no está disponible en ese horario; debe haber al menos 15 minutos entre citas.");
        }

        boolean choqueMascota = citaRepository.findByMascotaAndFechaHoraBetween(mascota, ini, fin).stream()
                .anyMatch(c -> !c.getId().equals(excluirId) && !ESTADO_CANCELADA.equalsIgnoreCase(c.getEstado()));
        if (choqueMascota) {
            throw new ConflictException("La mascota ya tiene una cita en ese horario (cita duplicada).");
        }
    }

    /**
     * Lista todas las citas registradas en el sistema.
     *
     * @return Lista de entidades Cita.
     */
    @Override
    public List<Cita> listarCitas() {
        return citaRepository.findAll();
    }

    /**
     * Lista citas dentro de un rango de fechas ordenadas cronológicamente.
     *
     * @param fechaInicio Fecha inicial.
     * @param fechaFin    Fecha final.
     * @return Lista de citas dentro del rango.
     */
    @Override
    public List<Cita> listarCitasRango(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        this.validarFechas(fechaInicio, fechaFin);
        List<Cita> citas = citaRepository.findByFechaHoraBetween(fechaInicio, fechaFin);
        citas.sort((c1, c2) -> c1.getFechaHora().compareTo(c2.getFechaHora()));
        return citas;
    }

    /**
     * Consulta una cita específica por su identificador.
     *
     * @param id Identificador de la cita.
     * @return Entidad Cita.
     */
    @Override
    public Cita consultarCita(Long id) {
        return citaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("La cita con ID " + id + " no existe."));
    }

    /**
     * Crea una nueva cita validando cliente, pertenencia de la mascota, disponibilidad y estado inicial.
     *
     * @param citaRq Objeto DTO con los datos de la nueva cita.
     * @return MiRespuestaRS Confirmación de la operación.
     */
    @Override
    @Transactional
    public MiRespuestaRS crearCita(CitaRq citaRq) {
        this.validarDatosCitaRq(citaRq);

        if (citaRq.getFechaHora().isBefore(LocalDateTime.now())) {
            throw new ConflictException("No se pueden programar citas en fechas u horas pasadas.");
        }

        if (citaRq.getClienteId() == null) {
            throw new BadRequestException("Debe seleccionar un cliente.");
        }

        Cliente cliente = clienteRepository.findById(citaRq.getClienteId())
                .orElseThrow(() -> new ResourceNotFoundException("El cliente con ID " + citaRq.getClienteId() + " no existe."));

        Mascota mascota = mascotaRepository.findById(citaRq.getMascotaId())
                .orElseThrow(() -> new ResourceNotFoundException("La mascota con ID " + citaRq.getMascotaId() + " no existe."));

        if (mascota.getCliente() == null || !mascota.getCliente().getClienteId().equals(cliente.getClienteId())) {
            throw new BadRequestException("La mascota seleccionada no pertenece al cliente indicado.");
        }

        Medico medico = medicoRepository.findById(citaRq.getMedicoId())
                .orElseThrow(() -> new ResourceNotFoundException("El médico con ID " + citaRq.getMedicoId() + " no existe."));

        String estado = (citaRq.getEstado() == null || citaRq.getEstado().isBlank())
                ? "PROGRAMADA" : citaRq.getEstado().trim().toUpperCase();

        if (!ESTADOS_INICIALES.contains(estado)) {
            throw new BadRequestException("El estado inicial de la cita debe ser PENDIENTE, PROGRAMADA o CONFIRMADA.");
        }

        validarDisponibilidad(medico, mascota, citaRq.getFechaHora(), -1L);

        Cita nuevaCita = new Cita();
        nuevaCita.setFechaHora(citaRq.getFechaHora());
        nuevaCita.setCliente(cliente);
        nuevaCita.setMascota(mascota);
        nuevaCita.setMedico(medico);
        nuevaCita.setMotivo(citaRq.getMotivo().trim());
        nuevaCita.setEstado(estado);

        citaRepository.save(nuevaCita);

        MiRespuestaRS rta = new MiRespuestaRS();
        rta.setStatus(200);
        rta.setMessage("Se agendó exitosamente la cita para la mascota "
                + mascota.getNombreMascota() + " con el médico Dr. " + medico.getNombres() + " "
                + medico.getApellidos() + " el " + citaRq.getFechaHora().toString());
        return rta;
    }

    /**
     * Actualiza la información de una cita existente (fecha, hora, médico, motivo) respetando su estado actual.
     *
     * @param citaRq Objeto DTO con los datos actualizados.
     * @return MiRespuestaRS Confirmación de la actualización.
     */
    @Override
    @Transactional
    public MiRespuestaRS actualizarCita(CitaRq citaRq) {
        this.validarDatosCitaRq(citaRq);
        if (citaRq.getId() == null) {
            throw new BadRequestException("El ID de la cita es obligatorio para actualizar.");
        }

        Cita cita = citaRepository.findById(citaRq.getId())
                .orElseThrow(() -> new ResourceNotFoundException("La cita con ID " + citaRq.getId() + " no existe."));

        if (ESTADO_CANCELADA.equalsIgnoreCase(cita.getEstado())) {
            throw new ConflictException("La cita está cancelada y no puede modificarse; use la opción de reactivar.");
        }
        if (ESTADO_ATENDIDA.equalsIgnoreCase(cita.getEstado())) {
            throw new ConflictException("La cita ya fue atendida; su información histórica no puede modificarse.");
        }
        if (!ESTADOS_EDITABLES.contains(cita.getEstado().toUpperCase())) {
            throw new ConflictException("La cita no puede modificarse en su estado actual.");
        }

        if (!cita.getMascota().getMascotaId().equals(citaRq.getMascotaId())) {
            throw new BadRequestException("No es posible cambiar la mascota de una cita; cancélela y cree una nueva.");
        }

        Medico medico = medicoRepository.findById(citaRq.getMedicoId())
                .orElseThrow(() -> new ResourceNotFoundException("El veterinario seleccionado no existe."));

        if (!citaRq.getFechaHora().equals(cita.getFechaHora()) && citaRq.getFechaHora().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("La fecha y hora no pueden ser anteriores al momento actual.");
        }

        validarDisponibilidad(medico, cita.getMascota(), citaRq.getFechaHora(), cita.getId());

        cita.setMedico(medico);
        cita.setFechaHora(citaRq.getFechaHora());
        cita.setMotivo(citaRq.getMotivo().trim());

        citaRepository.save(cita);

        MiRespuestaRS rta = new MiRespuestaRS();
        rta.setStatus(200);
        rta.setMessage("La cita fue actualizada correctamente.");
        return rta;
    }

    /**
     * Cambia el estado de una cita validando la matriz de transiciones permitidas.
     *
     * @param id          ID de la cita.
     * @param nuevoEstado Nuevo estado destino.
     * @return MiRespuestaRS Confirmación del cambio.
     */
    @Override
    @Transactional
    public MiRespuestaRS cambiarEstado(Long id, String nuevoEstado) {
        if (nuevoEstado == null || nuevoEstado.isBlank()) {
            throw new BadRequestException("Debe indicar el nuevo estado.");
        }
        String nuevo = nuevoEstado.trim().toUpperCase();
        Cita cita = consultarCita(id);
        String actual = cita.getEstado().toUpperCase();

        if (ESTADO_CANCELADA.equals(actual)) {
            throw new ConflictException("Una cita cancelada no puede cambiar de estado directamente; use la opción de reactivar.");
        }
        if (!TRANSICIONES.containsKey(nuevo)) {
            throw new BadRequestException("El estado indicado no es válido.");
        }
        if (!TRANSICIONES.getOrDefault(actual, Set.of()).contains(nuevo)) {
            throw new ConflictException("No se puede pasar una cita de estado " + actual + " a " + nuevo + ".");
        }

        cita.setEstado(nuevo);
        citaRepository.save(cita);

        MiRespuestaRS rta = new MiRespuestaRS();
        rta.setStatus(200);
        rta.setMessage("La cita quedó en estado " + nuevo + ".");
        return rta;
    }

    /**
     * Reactiva una cita previamente cancelada verificando fecha futura y disponibilidad de horario.
     *
     * @param id ID de la cita a reactivar.
     * @return MiRespuestaRS Confirmación de reactivación.
     */
    @Override
    @Transactional
    public MiRespuestaRS reactivar(Long id) {
        Cita cita = consultarCita(id);

        if (!ESTADO_CANCELADA.equalsIgnoreCase(cita.getEstado())) {
            throw new ConflictException("Solo las citas canceladas pueden reactivarse.");
        }
        if (cita.getFechaHora().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("La fecha programada para la cita ya pasó; cree una nueva cita.");
        }

        validarDisponibilidad(cita.getMedico(), cita.getMascota(), cita.getFechaHora(), cita.getId());

        cita.setEstado("PENDIENTE");
        citaRepository.save(cita);

        MiRespuestaRS rta = new MiRespuestaRS();
        rta.setStatus(200);
        rta.setMessage("La cita fue reactivada exitosamente en estado PENDIENTE.");
        return rta;
    }

    /**
     * Valida la coherencia de un rango de fechas.
     */
    private void validarFechas(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        if (fechaInicio == null || fechaFin == null) {
            throw new BadRequestException("Las fechas no pueden ser nulas.");
        }
        if (fechaInicio.isAfter(fechaFin)) {
            throw new BadRequestException("La fecha de inicio no puede ser posterior a la fecha de fin.");
        }
    }

    /**
     * Valida que los datos obligatorios del DTO de entrada no sean nulos o vacíos.
     */
    private void validarDatosCitaRq(CitaRq citaRq) {
        if (citaRq == null) {
            throw new BadRequestException("Los datos de la cita no pueden ser nulos.");
        }
        if (citaRq.getFechaHora() == null) {
            throw new BadRequestException("La fecha y hora de la cita son obligatorias.");
        }
        if (citaRq.getMascotaId() == null) {
            throw new BadRequestException("El ID de la mascota es obligatorio.");
        }
        if (citaRq.getMedicoId() == null) {
            throw new BadRequestException("El ID del médico es obligatorio.");
        }
        if (citaRq.getMotivo() == null || citaRq.getMotivo().trim().isEmpty()) {
            throw new BadRequestException("El motivo de la consulta es obligatorio.");
        }
    }
}