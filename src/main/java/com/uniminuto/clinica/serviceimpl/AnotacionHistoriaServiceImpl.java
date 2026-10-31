package com.uniminuto.clinica.serviceimpl;

import com.uniminuto.clinica.entity.AnotacionHistoria;
import com.uniminuto.clinica.entity.Cita;
import com.uniminuto.clinica.entity.HistoriaMedica;
import com.uniminuto.clinica.entity.Medico;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.exception.ConflictException;
import com.uniminuto.clinica.exception.NotFoundException;
import com.uniminuto.clinica.models.AnotacionHistoriaRq;
import com.uniminuto.clinica.models.AnotacionHistoriaRs;
import com.uniminuto.clinica.models.FiltroPaginadoRq;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.models.PageRs;
import com.uniminuto.clinica.repository.AnotacionHistoriaRepository;
import com.uniminuto.clinica.repository.CitaRepository;
import com.uniminuto.clinica.repository.FormulaMedicaRepository;
import com.uniminuto.clinica.repository.HistoriaMedicaRepository;
import com.uniminuto.clinica.repository.MascotaRepository;
import com.uniminuto.clinica.repository.MedicoRepository;
import com.uniminuto.clinica.service.AnotacionHistoriaService;
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
 * Implementación de las reglas de negocio de las anotaciones.
 * Una anotación siempre pertenece a una historia existente, nunca queda huérfana.
 */
@Service
public class AnotacionHistoriaServiceImpl implements AnotacionHistoriaService {

    /** Columnas por las que se puede ordenar el histórico (nombre lógico → propiedad). */
    private static final Map<String, String> ORDENABLES = Map.of(
            "fecha", "fecha",
            "veterinario", "medico.nombres",
            "descripcion", "descripcion",
            "diagnostico", "diagnostico",
            "observaciones", "observaciones");

    private final HistoriaMedicaRepository historiaMedicaRepository;
    private final AnotacionHistoriaRepository anotacionHistoriaRepository;
    private final MascotaRepository mascotaRepository;
    private final MedicoRepository medicoRepository;
    private final CitaRepository citaRepository;
    private final FormulaMedicaRepository formulaMedicaRepository;
    private final HistoriaService historiaService;

    /**
     * Crea el servicio con sus dependencias.
     *
     * @param historiaMedicaRepository    repositorio de historias.
     * @param anotacionHistoriaRepository repositorio de anotaciones.
     * @param mascotaRepository           repositorio de mascotas.
     * @param medicoRepository            repositorio de veterinarios.
     * @param citaRepository              repositorio de citas.
     * @param formulaMedicaRepository     repositorio de fórmulas.
     * @param historiaService             servicio de historias (crea la historia de una cita atendida).
     */
    public AnotacionHistoriaServiceImpl(HistoriaMedicaRepository historiaMedicaRepository,
                                        AnotacionHistoriaRepository anotacionHistoriaRepository,
                                        MascotaRepository mascotaRepository,
                                        MedicoRepository medicoRepository,
                                        CitaRepository citaRepository,
                                        FormulaMedicaRepository formulaMedicaRepository,
                                        HistoriaService historiaService) {
        this.historiaMedicaRepository = historiaMedicaRepository;
        this.anotacionHistoriaRepository = anotacionHistoriaRepository;
        this.mascotaRepository = mascotaRepository;
        this.medicoRepository = medicoRepository;
        this.citaRepository = citaRepository;
        this.formulaMedicaRepository = formulaMedicaRepository;
        this.historiaService = historiaService;
    }

    @Override
    @Transactional
    public MiRespuestaRS crearAnotacionHistoria(AnotacionHistoriaRq rq) {
        validarCampos(rq);
        HistoriaMedica historia = resolverHistoria(rq);
        Cita cita = historia.getCita();
        if (cita == null) {
            // Historias antiguas sin cita: se exige que el frontend envíe la cita de la anotación.
            if (rq.getCitaId() == null) {
                throw new BadRequestException("Debes indicar la cita de la anotación.");
            }
            cita = citaRepository.findById(rq.getCitaId())
                    .orElseThrow(() -> new NotFoundException("La cita no existe."));
            if (!cita.getMascota().getMascotaId().equals(historia.getMascota().getMascotaId())) {
                throw new BadRequestException("La cita seleccionada pertenece a otra mascota.");
            }
        } else if (rq.getCitaId() != null && !rq.getCitaId().equals(cita.getId())) {
            throw new BadRequestException("La cita indicada no corresponde a la historia clínica.");
        }
        Medico medico = medicoRepository.findById(rq.getMedicoId())
                .orElseThrow(() -> new NotFoundException("El veterinario no existe."));
        if (Boolean.FALSE.equals(medico.getEstado())) {
            throw new ConflictException("Veterinario no disponible: está inactivo.");
        }

        AnotacionHistoria anotacion = new AnotacionHistoria();
        anotacion.setHistoria(historia);
        anotacion.setCita(cita);
        anotacion.setMedico(medico);
        anotacion.setFecha(LocalDateTime.now());
        anotacion.setDescripcion(rq.getDescripcion().trim());
        anotacion.setDiagnostico(rq.getDiagnostico().trim());
        anotacion.setObservaciones(rq.getObservaciones().trim());
        anotacionHistoriaRepository.save(anotacion);

        MiRespuestaRS rta = new MiRespuestaRS();
        rta.setStatus(201);
        rta.setMessage("Anotación de historia clínica creada exitosamente");
        return rta;
    }

    @Override
    public PageRs<AnotacionHistoriaRs> listarPorHistoria(Long historiaId, FiltroPaginadoRq filtro) {
        if (historiaId == null || historiaId <= 0) {
            throw new BadRequestException("El id de la historia clínica debe ser un número mayor a cero.");
        }
        if (!historiaMedicaRepository.existsById(historiaId)) {
            throw new NotFoundException("No existe una historia clínica con el id " + historiaId + ".");
        }
        return listar(filtro, (root, hist) -> hist.get("id"), historiaId);
    }

    @Override
    public PageRs<AnotacionHistoriaRs> listarPorMascota(Integer mascotaId, FiltroPaginadoRq filtro) {
        if (mascotaId == null || mascotaId <= 0) {
            throw new BadRequestException("El id de la mascota debe ser un número mayor a cero.");
        }
        if (!mascotaRepository.existsById(mascotaId)) {
            throw new NotFoundException("La mascota no existe.");
        }
        return listar(filtro, (root, hist) -> hist.get("mascota").get("mascotaId"), mascotaId);
    }

    @Override
    public AnotacionHistoriaRs buscarPorId(Long id) {
        if (id == null || id <= 0) {
            throw new BadRequestException("El id de la anotación debe ser un número mayor a cero.");
        }
        AnotacionHistoria anotacion = anotacionHistoriaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("No existe una anotación con el id " + id + "."));
        AnotacionHistoriaRs rs = new AnotacionHistoriaRs(anotacion);
        if (anotacion.getCita() != null) {
            rs.setFormulasMedicas(formulaMedicaRepository.findByCita_IdInOrderByFechaCreacionRegistroDesc(
                    List.of(anotacion.getCita().getId())));
        }
        return rs;
    }

    /**
     * Arma el listado paginado de anotaciones filtrando por historia o por mascota.
     *
     * @param filtro    parámetros de página, orden y filtros.
     * @param selector  indica qué columna de la historia se compara (id de historia o de mascota).
     * @param valor     valor a comparar.
     */
    private PageRs<AnotacionHistoriaRs> listar(FiltroPaginadoRq filtro, SelectorHistoria selector, Object valor) {
        FiltroPaginadoRq f = filtro == null ? new FiltroPaginadoRq() : filtro;
        Pageable pageable = PaginacionUtil.crear(f.getPage(), f.getSize(), f.getSort(), f.getDir(),
                ORDENABLES, "fecha", Sort.Direction.DESC);
        if (f.getDesde() != null && f.getHasta() != null && f.getDesde().isAfter(f.getHasta())) {
            throw new BadRequestException("La fecha 'desde' no puede ser posterior a la fecha 'hasta'.");
        }
        String texto = PaginacionUtil.limpiar(f.getQ());

        Specification<AnotacionHistoria> spec = (root, query, cb) -> {
            Join<Object, Object> historia = root.join("historia", JoinType.INNER);
            Join<Object, Object> medico = root.join("medico", JoinType.LEFT);
            List<Predicate> filtros = new ArrayList<>();
            filtros.add(cb.equal(selector.expresion(root, historia), valor));
            if (texto != null) {
                filtros.add(cb.or(
                        PaginacionUtil.contiene(cb, root.get("descripcion"), texto),
                        PaginacionUtil.contiene(cb, root.get("diagnostico"), texto),
                        PaginacionUtil.contiene(cb, root.get("observaciones"), texto),
                        PaginacionUtil.contiene(cb, medico.get("nombres"), texto),
                        PaginacionUtil.contiene(cb, medico.get("apellidos"), texto)));
            }
            if (f.getMedicoId() != null) {
                filtros.add(cb.equal(medico.get("id"), f.getMedicoId()));
            }
            if (f.getDesde() != null) {
                filtros.add(cb.greaterThanOrEqualTo(root.get("fecha"), f.getDesde().atStartOfDay()));
            }
            if (f.getHasta() != null) {
                filtros.add(cb.lessThan(root.get("fecha"), f.getHasta().plusDays(1).atStartOfDay()));
            }
            return cb.and(filtros.toArray(new Predicate[0]));
        };
        Page<AnotacionHistoriaRs> pagina = anotacionHistoriaRepository.findAll(spec, pageable)
                .map(AnotacionHistoriaRs::new);
        return PageRs.de(pagina);
    }

    /**
     * Encuentra la historia a la que va la anotación: por su id, o la de la cita indicada.
     * Si la cita ya fue atendida y por datos antiguos no tenía historia, se crea en ese momento.
     */
    private HistoriaMedica resolverHistoria(AnotacionHistoriaRq rq) {
        if (rq.getHistoriaId() != null) {
            return historiaMedicaRepository.findById(rq.getHistoriaId())
                    .orElseThrow(() -> new NotFoundException("La historia clínica no existe."));
        }
        if (rq.getCitaId() == null) {
            throw new BadRequestException("Debes indicar la historia clínica o la cita de la anotación.");
        }
        Cita cita = citaRepository.findById(rq.getCitaId())
                .orElseThrow(() -> new NotFoundException("La cita con ID " + rq.getCitaId() + " no existe."));
        return historiaMedicaRepository.findByCita_Id(cita.getId()).orElseGet(() -> {
            if (!EstadoCita.ATENDIDA.equals(cita.getEstado())) {
                throw new ConflictException("La cita aún no tiene historia clínica. "
                        + "Regístrala primero o marca la cita como atendida.");
            }
            return historiaService.asegurarHistoriaDeCita(cita);
        });
    }

    private void validarCampos(AnotacionHistoriaRq rq) {
        if (rq == null) {
            throw new BadRequestException("La anotación de historia clínica es obligatoria");
        }
        if (rq.getMedicoId() == null) {
            throw new BadRequestException("El ID del médico es obligatorio");
        }
        if (esVacio(rq.getDescripcion())) {
            throw new BadRequestException("La descripción de la anotación es obligatoria");
        }
        if (esVacio(rq.getDiagnostico())) {
            throw new BadRequestException("El diagnóstico de la anotación es obligatorio");
        }
        if (esVacio(rq.getObservaciones())) {
            throw new BadRequestException("Las observaciones de la anotación son obligatorias");
        }
    }

    private boolean esVacio(String valor) {
        return valor == null || valor.trim().isEmpty();
    }

    /** Elige la columna de la historia contra la que se filtra (id de historia o id de mascota). */
    @FunctionalInterface
    private interface SelectorHistoria {
        /**
         * Devuelve la expresión a comparar.
         *
         * @param root     raíz de la consulta (anotación).
         * @param historia join con la historia.
         * @return la expresión sobre la que se aplica el filtro.
         */
        jakarta.persistence.criteria.Expression<?> expresion(
                jakarta.persistence.criteria.Root<AnotacionHistoria> root,
                Join<Object, Object> historia);
    }
}
