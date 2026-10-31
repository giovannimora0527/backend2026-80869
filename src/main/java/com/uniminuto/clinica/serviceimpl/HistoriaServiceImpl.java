package com.uniminuto.clinica.serviceimpl;

import com.uniminuto.clinica.entity.Cita;
import com.uniminuto.clinica.entity.Cliente;
import com.uniminuto.clinica.entity.FormulaMedica;
import com.uniminuto.clinica.entity.HistoriaMedica;
import com.uniminuto.clinica.entity.Mascota;
import com.uniminuto.clinica.entity.Medico;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.exception.ConflictException;
import com.uniminuto.clinica.exception.NotFoundException;
import com.uniminuto.clinica.models.AnotacionHistoriaRs;
import com.uniminuto.clinica.models.FiltroPaginadoRq;
import com.uniminuto.clinica.models.HistoriaMedicaRq;
import com.uniminuto.clinica.models.HistoriaMedicaRs;
import com.uniminuto.clinica.models.HistoriaResumenRs;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.models.PageRs;
import com.uniminuto.clinica.repository.AnotacionHistoriaRepository;
import com.uniminuto.clinica.repository.CitaRepository;
import com.uniminuto.clinica.repository.ClienteRepository;
import com.uniminuto.clinica.repository.FormulaMedicaRepository;
import com.uniminuto.clinica.repository.HistoriaMedicaRepository;
import com.uniminuto.clinica.repository.MascotaRepository;
import com.uniminuto.clinica.repository.MedicoRepository;
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
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Implementación de las reglas de negocio de las historias clínicas.
 * <p>
 * Modelo: una historia es el registro de una consulta (una por cita atendida) y puede
 * tener varias anotaciones. Se validan las relaciones cliente → mascota → cita → veterinario
 * para que no queden registros huérfanos o cruzados entre mascotas.
 */
@Service
public class HistoriaServiceImpl implements HistoriaService {

    /** Tolerancia (minutos) para relojes desajustados entre cliente y servidor. */
    private static final long TOLERANCIA_MINUTOS = 5;

    /** Columnas por las que se puede ordenar el listado (nombre lógico → propiedad). */
    private static final Map<String, String> ORDENABLES = Map.of(
            "fecha", "fechaConsulta",
            "mascota", "mascota.nombreMascota",
            "cliente", "mascota.cliente.nombres",
            "veterinario", "medico.nombres",
            "motivo", "motivo",
            "estado", "cita.estado");

    private final HistoriaMedicaRepository historiaMedicaRepository;
    private final AnotacionHistoriaRepository anotacionHistoriaRepository;
    private final FormulaMedicaRepository formulaMedicaRepository;
    private final MascotaRepository mascotaRepository;
    private final ClienteRepository clienteRepository;
    private final CitaRepository citaRepository;
    private final MedicoRepository medicoRepository;

    /**
     * Crea el servicio con sus dependencias.
     *
     * @param historiaMedicaRepository    repositorio de historias.
     * @param anotacionHistoriaRepository repositorio de anotaciones.
     * @param formulaMedicaRepository     repositorio de fórmulas.
     * @param mascotaRepository           repositorio de mascotas.
     * @param clienteRepository           repositorio de clientes.
     * @param citaRepository              repositorio de citas.
     * @param medicoRepository            repositorio de veterinarios.
     */
    public HistoriaServiceImpl(HistoriaMedicaRepository historiaMedicaRepository,
                               AnotacionHistoriaRepository anotacionHistoriaRepository,
                               FormulaMedicaRepository formulaMedicaRepository,
                               MascotaRepository mascotaRepository,
                               ClienteRepository clienteRepository,
                               CitaRepository citaRepository,
                               MedicoRepository medicoRepository) {
        this.historiaMedicaRepository = historiaMedicaRepository;
        this.anotacionHistoriaRepository = anotacionHistoriaRepository;
        this.formulaMedicaRepository = formulaMedicaRepository;
        this.mascotaRepository = mascotaRepository;
        this.clienteRepository = clienteRepository;
        this.citaRepository = citaRepository;
        this.medicoRepository = medicoRepository;
    }

    @Override
    public List<HistoriaMedicaRs> getAllHistorias() {
        List<HistoriaMedica> historias = historiaMedicaRepository.findAll();
        List<HistoriaMedicaRs> resultados = new ArrayList<>();
        if (!historias.isEmpty()) {
            resultados.addAll(historias.stream().map(this::armarHistoriaConAnotaciones).toList());
        }
        List<AnotacionHistoriaRs> anotaciones = resultados.stream()
                .flatMap(historia -> historia.getAnotaciones().stream()).toList();
        adjuntarFormulas(anotaciones);
        return resultados;
    }

    @Override
    public HistoriaMedicaRs buscarHistoriaPorId(Long id) {
        HistoriaMedica historia = obtenerHistoria(id);
        HistoriaMedicaRs rs = armarHistoriaConAnotaciones(historia);
        adjuntarFormulas(rs.getAnotaciones());
        return rs;
    }

    @Override
    public PageRs<HistoriaResumenRs> listarHistoriasPaginado(FiltroPaginadoRq filtro) {
        FiltroPaginadoRq f = filtro == null ? new FiltroPaginadoRq() : filtro;
        Pageable pageable = PaginacionUtil.crear(f.getPage(), f.getSize(), f.getSort(), f.getDir(),
                ORDENABLES, "fechaConsulta", Sort.Direction.DESC);
        String estado = EstadoCita.normalizar(f.getEstado());
        if (f.getDesde() != null && f.getHasta() != null && f.getDesde().isAfter(f.getHasta())) {
            throw new BadRequestException("La fecha 'desde' no puede ser posterior a la fecha 'hasta'.");
        }
        String texto = PaginacionUtil.limpiar(f.getQ());

        Specification<HistoriaMedica> spec = (root, query, cb) -> {
            Join<Object, Object> mascota = root.join("mascota", JoinType.LEFT);
            Join<Object, Object> cliente = mascota.join("cliente", JoinType.LEFT);
            Join<Object, Object> medico = root.join("medico", JoinType.LEFT);
            Join<Object, Object> cita = root.join("cita", JoinType.LEFT);
            List<Predicate> filtros = new ArrayList<>();
            if (texto != null) {
                filtros.add(cb.or(
                        PaginacionUtil.contiene(cb, mascota.get("nombreMascota"), texto),
                        PaginacionUtil.contiene(cb, cliente.get("nombres"), texto),
                        PaginacionUtil.contiene(cb, cliente.get("apellidos"), texto),
                        PaginacionUtil.contiene(cb, medico.get("nombres"), texto),
                        PaginacionUtil.contiene(cb, medico.get("apellidos"), texto),
                        PaginacionUtil.contiene(cb, root.get("motivo"), texto),
                        PaginacionUtil.contiene(cb, root.get("diagnostico"), texto)));
            }
            if (estado != null) {
                filtros.add(cb.equal(cita.get("estado"), estado));
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
                filtros.add(cb.greaterThanOrEqualTo(root.get("fechaConsulta"), f.getDesde().atStartOfDay()));
            }
            if (f.getHasta() != null) {
                filtros.add(cb.lessThan(root.get("fechaConsulta"), f.getHasta().plusDays(1).atStartOfDay()));
            }
            return cb.and(filtros.toArray(new Predicate[0]));
        };
        Page<HistoriaResumenRs> pagina = historiaMedicaRepository.findAll(spec, pageable)
                .map(h -> HistoriaResumenRs.de(h, anotacionHistoriaRepository.countByHistoria(h)));
        return PageRs.de(pagina);
    }

    @Override
    @Transactional
    public MiRespuestaRS guardarHistoria(HistoriaMedicaRq rq) throws BadRequestException {
        validarCamposObligatorios(rq, true);
        Cliente cliente = clienteRepository.findById(rq.getClienteId())
                .orElseThrow(() -> new NotFoundException("El cliente no existe."));
        Mascota mascota = mascotaRepository.findById(rq.getMascotaId())
                .orElseThrow(() -> new NotFoundException("La mascota no existe."));
        // Regla: la relación cliente → mascota se valida en el backend.
        if (mascota.getCliente() == null || !mascota.getCliente().getId().equals(cliente.getId())) {
            throw new BadRequestException("La mascota seleccionada no pertenece al cliente seleccionado.");
        }
        Cita cita = citaRepository.findById(rq.getCitaId())
                .orElseThrow(() -> new NotFoundException("La cita no existe."));
        // Regla: la cita debe ser de esa mascota (evita historias cruzadas entre mascotas).
        if (!cita.getMascota().getMascotaId().equals(mascota.getMascotaId())) {
            throw new BadRequestException("La cita seleccionada pertenece a otra mascota.");
        }
        if (EstadoCita.CANCELADA.equals(cita.getEstado())) {
            throw new ConflictException("No se puede registrar una historia clínica sobre una cita cancelada.");
        }
        if (cita.getFechaHora().isAfter(LocalDateTime.now())) {
            throw new ConflictException("La cita aún no ha ocurrido; no se puede registrar su historia clínica.");
        }
        if (historiaMedicaRepository.existsByCita_Id(cita.getId())) {
            throw new ConflictException("Esta cita ya tiene una historia clínica. "
                    + "Edítala o agrégale anotaciones.");
        }
        Medico medico = medicoRepository.findById(rq.getMedicoId())
                .orElseThrow(() -> new NotFoundException("El veterinario no existe."));
        if (Boolean.FALSE.equals(medico.getEstado())) {
            throw new ConflictException("Veterinario no disponible: está inactivo.");
        }
        if (!medico.getId().equals(cita.getMedico().getId())) {
            throw new BadRequestException("El veterinario debe ser el asignado a la cita.");
        }
        validarFechaConsulta(rq.getFechaHora(), cita);

        HistoriaMedica historia = new HistoriaMedica();
        historia.setMascota(mascota);
        historia.setCita(cita);
        historia.setMedico(medico);
        historia.setFechaConsulta(rq.getFechaHora());
        historia.setFechaCreacion(LocalDateTime.now());
        historia.setMotivo(rq.getMotivo().trim());
        historia.setDiagnostico(rq.getDiagnostico().trim());
        historia.setObservaciones(rq.getObservaciones().trim());
        historiaMedicaRepository.save(historia);

        // Regla: registrar la historia de una cita la deja como atendida.
        if (EstadoCita.ACTIVAS.contains(cita.getEstado())) {
            cita.setEstado(EstadoCita.ATENDIDA);
            citaRepository.save(cita);
        }
        return respuesta(201, "Historia clínica guardada correctamente.");
    }

    @Override
    @Transactional
    public MiRespuestaRS actualizarHistoria(HistoriaMedicaRq rq) throws BadRequestException {
        if (rq == null || rq.getId() == null || rq.getId() <= 0) {
            throw new BadRequestException("El id de la historia clínica es obligatorio y debe ser mayor a cero.");
        }
        validarCamposObligatorios(rq, false);
        HistoriaMedica historia = obtenerHistoria(rq.getId());
        // Regla: a quién se atendió, en qué cita y quién lo hizo no cambia; solo el contenido clínico.
        if (rq.getMascotaId() != null && !rq.getMascotaId().equals(historia.getMascota().getMascotaId())) {
            throw new ConflictException("No se puede cambiar la mascota de una historia clínica.");
        }
        if (rq.getCitaId() != null && historia.getCita() != null
                && !rq.getCitaId().equals(historia.getCita().getId())) {
            throw new ConflictException("No se puede cambiar la cita de una historia clínica.");
        }
        if (rq.getMedicoId() != null && historia.getMedico() != null
                && !rq.getMedicoId().equals(historia.getMedico().getId())) {
            throw new ConflictException("No se puede cambiar el veterinario de una historia clínica.");
        }
        historia.setMotivo(rq.getMotivo().trim());
        historia.setDiagnostico(rq.getDiagnostico().trim());
        historia.setObservaciones(rq.getObservaciones().trim());
        historiaMedicaRepository.save(historia);
        return respuesta(200, "Historia clínica actualizada correctamente.");
    }

    @Override
    @Transactional
    public HistoriaMedica asegurarHistoriaDeCita(Cita cita) {
        return historiaMedicaRepository.findByCita_Id(cita.getId()).orElseGet(() -> {
            HistoriaMedica nueva = new HistoriaMedica();
            nueva.setMascota(cita.getMascota());
            nueva.setCita(cita);
            nueva.setMedico(cita.getMedico());
            nueva.setFechaConsulta(cita.getFechaHora());
            nueva.setFechaCreacion(LocalDateTime.now());
            nueva.setMotivo(cita.getMotivo());
            return historiaMedicaRepository.save(nueva);
        });
    }

    private HistoriaMedica obtenerHistoria(Long id) {
        if (id == null || id <= 0) {
            throw new BadRequestException("El id de la historia clínica debe ser un número mayor a cero.");
        }
        return historiaMedicaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("No existe una historia clínica con el id " + id + "."));
    }

    private HistoriaMedicaRs armarHistoriaConAnotaciones(HistoriaMedica historia) {
        HistoriaMedicaRs rs = new HistoriaMedicaRs();
        rs.setHistoria(historia);
        rs.setAnotaciones(anotacionHistoriaRepository.findByHistoria(historia).stream()
                .map(AnotacionHistoriaRs::new).toList());
        return rs;
    }

    /** Agrega a cada anotación las fórmulas de su cita, consultándolas en una sola query. */
    private void adjuntarFormulas(List<AnotacionHistoriaRs> anotaciones) {
        List<Long> citaIds = anotaciones.stream()
                .map(AnotacionHistoriaRs::getCita)
                .filter(Objects::nonNull)
                .map(Cita::getId)
                .distinct().toList();
        if (citaIds.isEmpty()) {
            return;
        }
        Map<Long, List<FormulaMedica>> formulasPorCita = formulaMedicaRepository
                .findByCita_IdInOrderByFechaCreacionRegistroDesc(citaIds).stream()
                .collect(Collectors.groupingBy(formula -> formula.getCita().getId()));
        anotaciones.stream().filter(anotacion -> anotacion.getCita() != null)
                .forEach(anotacion -> anotacion.setFormulasMedicas(
                        formulasPorCita.getOrDefault(anotacion.getCita().getId(), List.of())));
    }

    /** La consulta no puede ser anterior a su cita ni estar en el futuro. */
    private void validarFechaConsulta(LocalDateTime fechaConsulta, Cita cita) {
        if (fechaConsulta.isBefore(cita.getFechaHora())) {
            throw new BadRequestException("La fecha de la consulta no puede ser anterior a la fecha de la cita.");
        }
        if (fechaConsulta.isAfter(LocalDateTime.now().plusMinutes(TOLERANCIA_MINUTOS))) {
            throw new BadRequestException("La fecha de la consulta no puede estar en el futuro.");
        }
    }

    /**
     * Valida los campos obligatorios. Al crear se exigen todas las relaciones;
     * al editar solo el contenido clínico.
     */
    private void validarCamposObligatorios(HistoriaMedicaRq rq, boolean creando) {
        if (rq == null) {
            throw new BadRequestException("La información de la historia clínica es obligatoria.");
        }
        if (creando) {
            if (rq.getClienteId() == null) {
                throw new BadRequestException("Debes seleccionar el cliente.");
            }
            if (rq.getMascotaId() == null || rq.getMascotaId() <= 0) {
                throw new BadRequestException("Debes seleccionar la mascota.");
            }
            if (rq.getCitaId() == null || rq.getCitaId() <= 0) {
                throw new BadRequestException("Debes seleccionar la cita de la consulta.");
            }
            if (rq.getMedicoId() == null || rq.getMedicoId() <= 0) {
                throw new BadRequestException("Debes seleccionar el veterinario.");
            }
            if (rq.getFechaHora() == null) {
                throw new BadRequestException("La fecha y hora de la consulta es obligatoria.");
            }
        }
        if (esVacio(rq.getMotivo())) {
            throw new BadRequestException("El motivo de la consulta es obligatorio.");
        }
        if (esVacio(rq.getDiagnostico())) {
            throw new BadRequestException("El diagnóstico es obligatorio.");
        }
        if (esVacio(rq.getObservaciones())) {
            throw new BadRequestException("Las observaciones son obligatorias.");
        }
    }

    private boolean esVacio(String valor) {
        return valor == null || valor.trim().isEmpty();
    }

    private MiRespuestaRS respuesta(int status, String mensaje) {
        MiRespuestaRS rs = new MiRespuestaRS();
        rs.setStatus(status);
        rs.setMessage(mensaje);
        return rs;
    }
}
