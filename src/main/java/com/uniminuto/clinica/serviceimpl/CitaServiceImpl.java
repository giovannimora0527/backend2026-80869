package com.uniminuto.clinica.serviceimpl;

import com.uniminuto.clinica.entity.Cita;
import com.uniminuto.clinica.entity.Cliente;
import com.uniminuto.clinica.entity.Mascota;
import com.uniminuto.clinica.entity.Medico;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.exception.ConflictException;
import com.uniminuto.clinica.exception.NotFoundException;
import com.uniminuto.clinica.models.CitaRq;
import com.uniminuto.clinica.models.CitaRs;
import com.uniminuto.clinica.models.FiltroPaginadoRq;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.models.PageRs;
import com.uniminuto.clinica.repository.CitaRepository;
import com.uniminuto.clinica.repository.ClienteRepository;
import com.uniminuto.clinica.repository.MascotaRepository;
import com.uniminuto.clinica.repository.MedicoRepository;
import com.uniminuto.clinica.service.CitaService;
import com.uniminuto.clinica.service.HistoriaService;
import com.uniminuto.clinica.utils.EstadoCita;
import com.uniminuto.clinica.utils.PaginacionUtil;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Implementación de las reglas de negocio de las citas.
 * <p>
 * Todas las reglas críticas viven aquí (backend); el formulario de Angular solo las refleja
 * para dar una mejor experiencia al usuario.
 */
@Service
public class CitaServiceImpl implements CitaService {

    /** Minutos antes y después de una cita en los que el veterinario o la mascota quedan ocupados. */
    private static final long VENTANA_MINUTOS = 15;

    /** Largo máximo del motivo de la consulta. */
    private static final int LARGO_MAXIMO_MOTIVO = 500;

    /** Columnas por las que se puede ordenar el listado (nombre lógico → propiedad). */
    private static final Map<String, String> ORDENABLES = Map.of(
            "fecha", "fechaHora",
            "hora", "fechaHora",
            "cliente", "cliente.nombres",
            "mascota", "mascota.nombreMascota",
            "veterinario", "medico.nombres",
            "estado", "estado",
            "motivo", "motivo");

    private final CitaRepository citaRepository;
    private final MascotaRepository mascotaRepository;
    private final MedicoRepository medicoRepository;
    private final ClienteRepository clienteRepository;
    private final HistoriaService historiaService;

    /**
     * Crea el servicio con sus dependencias.
     *
     * @param citaRepository     repositorio de citas.
     * @param mascotaRepository  repositorio de mascotas.
     * @param medicoRepository   repositorio de veterinarios.
     * @param clienteRepository  repositorio de clientes.
     * @param historiaService    servicio que crea la historia clínica de una cita atendida.
     */
    public CitaServiceImpl(CitaRepository citaRepository, MascotaRepository mascotaRepository,
                           MedicoRepository medicoRepository, ClienteRepository clienteRepository,
                           HistoriaService historiaService) {
        this.citaRepository = citaRepository;
        this.mascotaRepository = mascotaRepository;
        this.medicoRepository = medicoRepository;
        this.clienteRepository = clienteRepository;
        this.historiaService = historiaService;
    }

    @Override
    public List<Cita> listarCitas() throws BadRequestException {
        return citaRepository.findAll(Sort.by(Sort.Direction.DESC, "fechaHora"));
    }

    @Override
    public List<Cita> listarCitasRango(LocalDateTime fechaInicio, LocalDateTime fechaFin)
            throws BadRequestException {
        if (fechaInicio == null || fechaFin == null) {
            throw new BadRequestException("Las fechas no pueden ser nulas");
        }
        if (fechaInicio.isAfter(fechaFin)) {
            throw new BadRequestException("La fecha de inicio no puede ser mayor a la fecha de fin");
        }
        List<Cita> citas = new ArrayList<>(citaRepository.findByFechaHoraBetween(fechaInicio, fechaFin));
        citas.sort((c1, c2) -> c1.getFechaHora().compareTo(c2.getFechaHora()));
        return citas;
    }

    @Override
    public List<Cita> listarCitasPorMascota(Integer mascotaId) throws BadRequestException {
        if (mascotaId == null || mascotaId <= 0) {
            throw new BadRequestException("El id de la mascota es obligatorio y debe ser mayor a cero.");
        }
        Mascota mascota = obtenerMascota(mascotaId);
        return citaRepository.findByMascotaOrderByFechaHoraDesc(mascota);
    }

    @Override
    public PageRs<CitaRs> listarCitasPaginado(FiltroPaginadoRq filtro) {
        FiltroPaginadoRq f = filtro == null ? new FiltroPaginadoRq() : filtro;
        Pageable pageable = PaginacionUtil.crear(f.getPage(), f.getSize(), f.getSort(), f.getDir(),
                ORDENABLES, "fechaHora", Sort.Direction.DESC);
        String estado = EstadoCita.normalizar(f.getEstado());
        if (estado != null && !EstadoCita.TODOS.contains(estado)) {
            throw new BadRequestException("El estado '" + f.getEstado() + "' no es válido.");
        }
        if (f.getDesde() != null && f.getHasta() != null && f.getDesde().isAfter(f.getHasta())) {
            throw new BadRequestException("La fecha 'desde' no puede ser posterior a la fecha 'hasta'.");
        }
        String texto = PaginacionUtil.limpiar(f.getQ());

        Specification<Cita> spec = (root, query, cb) -> {
            Join<Object, Object> cliente = root.join("cliente", JoinType.LEFT);
            Join<Object, Object> mascota = root.join("mascota", JoinType.LEFT);
            Join<Object, Object> medico = root.join("medico", JoinType.LEFT);
            List<Predicate> filtros = new ArrayList<>();
            if (texto != null) {
                filtros.add(cb.or(
                        PaginacionUtil.contiene(cb, cliente.get("nombres"), texto),
                        PaginacionUtil.contiene(cb, cliente.get("apellidos"), texto),
                        PaginacionUtil.contiene(cb, cliente.get("numeroDocumento"), texto),
                        PaginacionUtil.contiene(cb, mascota.get("nombreMascota"), texto),
                        PaginacionUtil.contiene(cb, medico.get("nombres"), texto),
                        PaginacionUtil.contiene(cb, medico.get("apellidos"), texto),
                        PaginacionUtil.contiene(cb, root.get("motivo"), texto)));
            }
            if (estado != null) {
                filtros.add(cb.equal(root.get("estado"), estado));
            }
            if (f.getClienteId() != null) {
                filtros.add(cb.equal(cliente.get("id"), f.getClienteId()));
            }
            if (f.getMascotaId() != null) {
                filtros.add(cb.equal(mascota.get("mascotaId"), f.getMascotaId()));
            }
            if (f.getMedicoId() != null) {
                filtros.add(cb.equal(medico.get("id"), f.getMedicoId()));
            }
            if (f.getDesde() != null) {
                filtros.add(cb.greaterThanOrEqualTo(root.get("fechaHora"), f.getDesde().atStartOfDay()));
            }
            if (f.getHasta() != null) {
                filtros.add(cb.lessThan(root.get("fechaHora"), f.getHasta().plusDays(1).atStartOfDay()));
            }
            return cb.and(filtros.toArray(new Predicate[0]));
        };
        Page<CitaRs> pagina = citaRepository.findAll(spec, pageable).map(CitaRs::de);
        return PageRs.de(pagina);
    }

    @Override
    public Cita buscarCitaPorId(Long id) {
        if (id == null || id <= 0) {
            throw new BadRequestException("El id de la cita debe ser un número mayor a cero.");
        }
        return citaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("No existe una cita con el id " + id + "."));
    }

    @Override
    @Transactional
    public MiRespuestaRS crearCita(CitaRq rq) throws BadRequestException {
        validarDatos(rq);
        // Regla: no se registran citas en fechas anteriores a la actual.
        if (rq.getFechaHora().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("La fecha y hora de la cita no puede ser anterior a la fecha y hora actual");
        }
        Mascota mascota = obtenerMascota(rq.getMascotaId());
        Cliente cliente = resolverCliente(rq.getClienteId(), mascota);
        if (Boolean.FALSE.equals(cliente.getActivo())) {
            throw new ConflictException("El cliente está inactivo; no se le pueden agendar citas.");
        }
        if (Boolean.FALSE.equals(mascota.getActivo())) {
            throw new ConflictException("La mascota está inactiva; no se le pueden agendar citas.");
        }
        Medico medico = obtenerMedicoDisponible(rq.getMedicoId());

        String estado = EstadoCita.normalizar(rq.getEstado());
        if (estado == null) {
            estado = EstadoCita.PROGRAMADA;
        }
        // Una cita nueva no puede nacer atendida ni cancelada.
        if (!EstadoCita.CREABLES.contains(estado)) {
            throw new BadRequestException("Una cita nueva solo puede crearse como Pendiente, Programada o Confirmada.");
        }
        validarDisponibilidad(medico.getId(), mascota.getMascotaId(), rq.getFechaHora(), -1L);

        Cita cita = new Cita();
        cita.setCliente(cliente);
        cita.setMascota(mascota);
        cita.setMedico(medico);
        cita.setFechaHora(rq.getFechaHora());
        cita.setMotivo(rq.getMotivo().trim());
        cita.setEstado(estado);
        citaRepository.save(cita);

        return respuesta(201, "Se agendó una nueva cita para la mascota " + mascota.getNombreMascota()
                + " con el veterinario " + medico.getNombres() + " " + medico.getApellidos()
                + " para el día " + rq.getFechaHora() + ".");
    }

    @Override
    @Transactional
    public MiRespuestaRS actualizarCita(CitaRq rq) throws BadRequestException {
        if (rq == null || rq.getId() == null || rq.getId() <= 0) {
            throw new BadRequestException("El id de la cita es obligatorio y debe ser mayor a cero.");
        }
        validarDatos(rq);
        Cita cita = buscarCitaPorId(rq.getId());
        String estadoActual = cita.getEstado();

        // Reglas de bloqueo: estas citas son parte del historial y no se tocan.
        if (EstadoCita.CANCELADA.equals(estadoActual)) {
            throw new ConflictException("Una cita cancelada no puede modificarse. "
                    + "Si quieres retomarla, usa la opción de reactivar.");
        }
        if (EstadoCita.ATENDIDA.equals(estadoActual)) {
            throw new ConflictException("Una cita atendida no puede modificarse porque hace parte del historial clínico.");
        }
        // Cliente y mascota definen a quién se atiende: se cambian cancelando y creando otra cita.
        if (rq.getMascotaId() != null && !rq.getMascotaId().equals(cita.getMascota().getMascotaId())) {
            throw new BadRequestException("No se puede cambiar la mascota de una cita. Cancélala y crea una nueva.");
        }
        if (rq.getClienteId() != null && !rq.getClienteId().equals(cita.getCliente().getId())) {
            throw new BadRequestException("No se puede cambiar el cliente de una cita. Cancélala y crea una nueva.");
        }
        String estadoNuevo = EstadoCita.normalizar(rq.getEstado());
        if (estadoNuevo == null) {
            estadoNuevo = estadoActual;
        }
        validarEstadoExiste(estadoNuevo);

        Medico medico = cita.getMedico();
        if (!rq.getMedicoId().equals(medico.getId())) {
            medico = obtenerMedicoDisponible(rq.getMedicoId());
        }
        boolean cambioAgenda = !rq.getFechaHora().withNano(0).equals(cita.getFechaHora().withNano(0))
                || !medico.getId().equals(cita.getMedico().getId());
        if (cambioAgenda && !EstadoCita.CANCELADA.equals(estadoNuevo)) {
            if (rq.getFechaHora().isBefore(LocalDateTime.now())) {
                throw new BadRequestException("La fecha y hora de la cita no puede ser anterior a la fecha y hora actual");
            }
            validarDisponibilidad(medico.getId(), cita.getMascota().getMascotaId(), rq.getFechaHora(), cita.getId());
        }
        cita.setMedico(medico);
        cita.setFechaHora(rq.getFechaHora());
        cita.setMotivo(rq.getMotivo().trim());
        aplicarEstado(cita, estadoNuevo);
        citaRepository.save(cita);
        generarHistoriaSiFueAtendida(cita, estadoActual);

        return respuesta(200, "La cita se actualizó correctamente.");
    }

    @Override
    @Transactional
    public MiRespuestaRS cambiarEstadoCita(Long id, String estado) {
        String estadoNuevo = EstadoCita.normalizar(estado);
        if (estadoNuevo == null) {
            throw new BadRequestException("Debes indicar el nuevo estado de la cita.");
        }
        validarEstadoExiste(estadoNuevo);
        Cita cita = buscarCitaPorId(id);
        String estadoActual = cita.getEstado();
        if (estadoActual.equals(estadoNuevo)) {
            return respuesta(200, "La cita ya estaba en estado " + estadoNuevo + ".");
        }
        aplicarEstado(cita, estadoNuevo);
        citaRepository.save(cita);
        generarHistoriaSiFueAtendida(cita, estadoActual);
        return respuesta(200, "La cita ahora está en estado " + estadoNuevo + ".");
    }

    @Override
    @Transactional
    public MiRespuestaRS reactivarCita(Long id) {
        Cita cita = buscarCitaPorId(id);
        if (!EstadoCita.CANCELADA.equals(cita.getEstado())) {
            throw new ConflictException("Solo se pueden reactivar citas canceladas.");
        }
        if (cita.getFechaHora().isBefore(LocalDateTime.now())) {
            throw new ConflictException("La fecha de la cita ya pasó. Crea una nueva cita con otra fecha.");
        }
        if (Boolean.FALSE.equals(cita.getCliente().getActivo()) || Boolean.FALSE.equals(cita.getMascota().getActivo())) {
            throw new ConflictException("El cliente o la mascota están inactivos; no se puede reactivar la cita.");
        }
        if (Boolean.FALSE.equals(cita.getMedico().getEstado())) {
            throw new ConflictException("Veterinario no disponible: el veterinario de la cita está inactivo.");
        }
        // Mientras estuvo cancelada otra cita pudo tomar el horario: se vuelve a validar.
        validarDisponibilidad(cita.getMedico().getId(), cita.getMascota().getMascotaId(), cita.getFechaHora(), cita.getId());
        cita.setEstado(EstadoCita.PROGRAMADA);
        citaRepository.save(cita);
        return respuesta(200, "La cita fue reactivada y quedó Programada.");
    }

    /**
     * Cambia el estado de una cita aplicando las reglas de transición.
     * No guarda: el llamador es quien persiste.
     */
    private void aplicarEstado(Cita cita, String estadoNuevo) {
        String estadoActual = cita.getEstado();
        if (estadoActual.equals(estadoNuevo)) {
            return;
        }
        if (!EstadoCita.transicionPermitida(estadoActual, estadoNuevo)) {
            throw new ConflictException("No se puede pasar una cita de " + estadoActual + " a " + estadoNuevo + ".");
        }
        // Una cita solo puede estar atendida si ya ocurrió.
        if (EstadoCita.ATENDIDA.equals(estadoNuevo) && cita.getFechaHora().isAfter(LocalDateTime.now())) {
            throw new ConflictException("No se puede marcar como atendida una cita que aún no ha ocurrido.");
        }
        cita.setEstado(estadoNuevo);
    }

    /** Regla: una cita que pasa a atendida genera (o conserva) su historia clínica. */
    private void generarHistoriaSiFueAtendida(Cita cita, String estadoAnterior) {
        if (EstadoCita.ATENDIDA.equals(cita.getEstado()) && !EstadoCita.ATENDIDA.equals(estadoAnterior)) {
            historiaService.asegurarHistoriaDeCita(cita);
        }
    }

    /**
     * Verifica que ni el veterinario ni la mascota tengan otra cita (no cancelada)
     * entre 15 minutos antes y 15 minutos después de la hora pedida.
     */
    private void validarDisponibilidad(Long medicoId, Integer mascotaId, LocalDateTime fechaHora, Long excluirId) {
        LocalDateTime desde = fechaHora.minusMinutes(VENTANA_MINUTOS);
        LocalDateTime hasta = fechaHora.plusMinutes(VENTANA_MINUTOS);
        if (citaRepository.contarPorMedicoEnRango(medicoId, desde, hasta, EstadoCita.CANCELADA, excluirId) > 0) {
            throw new ConflictException("Veterinario no disponible: ya tiene una cita entre 15 minutos antes "
                    + "y 15 minutos después de esa hora.");
        }
        if (citaRepository.contarPorMascotaEnRango(mascotaId, desde, hasta, EstadoCita.CANCELADA, excluirId) > 0) {
            throw new ConflictException("Cita duplicada: la mascota ya tiene otra cita en ese horario.");
        }
    }

    private Mascota obtenerMascota(Integer mascotaId) {
        return mascotaRepository.findById(mascotaId)
                .orElseThrow(() -> new NotFoundException("La mascota no existe."));
    }

    private Medico obtenerMedicoDisponible(Long medicoId) {
        Medico medico = medicoRepository.findById(medicoId)
                .orElseThrow(() -> new NotFoundException("El veterinario no existe."));
        if (Boolean.FALSE.equals(medico.getEstado())) {
            throw new ConflictException("Veterinario no disponible: está inactivo.");
        }
        return medico;
    }

    /**
     * Resuelve el cliente de la cita y valida que la mascota le pertenezca.
     * Si el frontend no envía el cliente se toma el dueño de la mascota.
     */
    private Cliente resolverCliente(Long clienteId, Mascota mascota) {
        Cliente duenio = mascota.getCliente();
        if (clienteId == null) {
            if (duenio == null) {
                throw new BadRequestException("La mascota no pertenece a un cliente registrado.");
            }
            return duenio;
        }
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new NotFoundException("El cliente no existe."));
        if (duenio == null || !duenio.getId().equals(cliente.getId())) {
            throw new BadRequestException("La mascota seleccionada no pertenece al cliente seleccionado.");
        }
        return cliente;
    }

    private void validarEstadoExiste(String estado) {
        if (!EstadoCita.TODOS.contains(estado)) {
            throw new BadRequestException("El estado '" + estado + "' no es válido. Usa Pendiente, Programada, "
                    + "Confirmada, Atendida o Cancelada.");
        }
    }

    /** Valida los campos obligatorios (la misma validación se repite en el formulario Angular). */
    private void validarDatos(CitaRq rq) {
        if (rq == null) {
            throw new BadRequestException("La información de la cita es obligatoria.");
        }
        if (rq.getFechaHora() == null) {
            throw new BadRequestException("La fecha y hora de la cita no puede ser nula");
        }
        if (rq.getMascotaId() == null && rq.getId() == null) {
            throw new BadRequestException("Debes seleccionar la mascota de la cita.");
        }
        if (rq.getMedicoId() == null) {
            throw new BadRequestException("Debes seleccionar el veterinario de la cita.");
        }
        if (rq.getMotivo() == null || rq.getMotivo().trim().isEmpty()) {
            throw new BadRequestException("El motivo de la cita es obligatorio.");
        }
        if (rq.getMotivo().trim().length() > LARGO_MAXIMO_MOTIVO) {
            throw new BadRequestException("El motivo no puede superar los " + LARGO_MAXIMO_MOTIVO + " caracteres.");
        }
    }

    private MiRespuestaRS respuesta(int status, String mensaje) {
        MiRespuestaRS rs = new MiRespuestaRS();
        rs.setStatus(status);
        rs.setMessage(mensaje);
        return rs;
    }
}
