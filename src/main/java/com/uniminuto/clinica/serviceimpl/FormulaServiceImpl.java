package com.uniminuto.clinica.serviceimpl;

import com.uniminuto.clinica.entity.Cita;
import com.uniminuto.clinica.entity.FormulaMedica;
import com.uniminuto.clinica.entity.Mascota;
import com.uniminuto.clinica.entity.Medicamento;
import com.uniminuto.clinica.entity.Medico;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.exception.ConflictException;
import com.uniminuto.clinica.exception.NotFoundException;
import com.uniminuto.clinica.models.FiltroPaginadoRq;
import com.uniminuto.clinica.models.FormulaResumenRs;
import com.uniminuto.clinica.models.FormulaRq;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.models.PageRs;
import com.uniminuto.clinica.repository.CitaRepository;
import com.uniminuto.clinica.repository.FormulaMedicaRepository;
import com.uniminuto.clinica.repository.HistoriaMedicaRepository;
import com.uniminuto.clinica.repository.MascotaRepository;
import com.uniminuto.clinica.repository.MedicamentoRepository;
import com.uniminuto.clinica.repository.MedicoRepository;
import com.uniminuto.clinica.service.FormulaService;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Implementación de las reglas de negocio de las fórmulas médicas.
 * <p>
 * Principio general: una fórmula nunca se borra, solo se inactiva, porque ya forma parte
 * de la información clínica de la mascota.
 */
@Service
public class FormulaServiceImpl implements FormulaService {

    /** Tolerancia (minutos) para relojes desajustados entre cliente y servidor. */
    private static final long TOLERANCIA_MINUTOS = 5;

    /** Dosis: número (decimal o fracción como 1/2) seguido de una unidad, por ejemplo "0.5 ml". */
    private static final Pattern PATRON_DOSIS =
            Pattern.compile("^(\\d+(?:[.,]\\d+)?)(?:/(\\d+))?\\s*[\\p{L}µ%][\\p{L}µ%/.\\s]{0,38}$");

    /** Duración: número entero con unidad opcional (sin unidad se asumen días). */
    private static final Pattern PATRON_DURACION =
            Pattern.compile("^(\\d{1,3})\\s*(horas?|d[ií]as?|semanas?|mes(?:es)?)?$", Pattern.CASE_INSENSITIVE);

    /** Duración máxima de un tratamiento, expresada en días. */
    private static final int DURACION_MAXIMA_DIAS = 365;

    /** Largo máximo de la frecuencia. */
    private static final int LARGO_MAXIMO_FRECUENCIA = 100;

    /** Largo máximo de las indicaciones. */
    private static final int LARGO_MAXIMO_INDICACIONES = 1000;

    /** Columnas por las que se puede ordenar el listado (nombre lógico → propiedad). */
    private static final Map<String, String> ORDENABLES = Map.of(
            "fechaCreacion", "fechaCreacionRegistro",
            "fechaEmision", "fechaEmision",
            "mascota", "mascota.nombreMascota",
            "cliente", "mascota.cliente.nombres",
            "veterinario", "medico.nombres",
            "medicamento", "medicamento.nombre",
            "estado", "activo");

    private final FormulaMedicaRepository formulaMedicaRepository;
    private final CitaRepository citaRepository;
    private final MedicamentoRepository medicamentoRepository;
    private final MascotaRepository mascotaRepository;
    private final MedicoRepository medicoRepository;
    private final HistoriaMedicaRepository historiaMedicaRepository;

    /**
     * Crea el servicio con sus dependencias.
     *
     * @param formulaMedicaRepository  repositorio de fórmulas.
     * @param citaRepository           repositorio de citas.
     * @param medicamentoRepository    repositorio de medicamentos.
     * @param mascotaRepository        repositorio de mascotas.
     * @param medicoRepository         repositorio de veterinarios.
     * @param historiaMedicaRepository repositorio de historias (para enlazar la fórmula con su historia).
     */
    public FormulaServiceImpl(FormulaMedicaRepository formulaMedicaRepository,
                              CitaRepository citaRepository,
                              MedicamentoRepository medicamentoRepository,
                              MascotaRepository mascotaRepository,
                              MedicoRepository medicoRepository,
                              HistoriaMedicaRepository historiaMedicaRepository) {
        this.formulaMedicaRepository = formulaMedicaRepository;
        this.citaRepository = citaRepository;
        this.medicamentoRepository = medicamentoRepository;
        this.mascotaRepository = mascotaRepository;
        this.medicoRepository = medicoRepository;
        this.historiaMedicaRepository = historiaMedicaRepository;
    }

    @Override
    public List<FormulaMedica> findAllFormulas() {
        return formulaMedicaRepository.findAllByOrderByFechaCreacionRegistroDesc();
    }

    @Override
    public PageRs<FormulaResumenRs> listarFormulasPaginado(FiltroPaginadoRq filtro) {
        FiltroPaginadoRq f = filtro == null ? new FiltroPaginadoRq() : filtro;
        Pageable pageable = PaginacionUtil.crear(f.getPage(), f.getSize(), f.getSort(), f.getDir(),
                ORDENABLES, "fechaCreacionRegistro", Sort.Direction.DESC);
        if (f.getDesde() != null && f.getHasta() != null && f.getDesde().isAfter(f.getHasta())) {
            throw new BadRequestException("La fecha 'desde' no puede ser posterior a la fecha 'hasta'.");
        }
        String texto = PaginacionUtil.limpiar(f.getQ());

        Specification<FormulaMedica> spec = (root, query, cb) -> {
            Join<Object, Object> mascota = root.join("mascota", JoinType.LEFT);
            Join<Object, Object> cliente = mascota.join("cliente", JoinType.LEFT);
            Join<Object, Object> medico = root.join("medico", JoinType.LEFT);
            Join<Object, Object> medicamento = root.join("medicamento", JoinType.LEFT);
            List<Predicate> filtros = new ArrayList<>();
            if (texto != null) {
                filtros.add(cb.or(
                        PaginacionUtil.contiene(cb, mascota.get("nombreMascota"), texto),
                        PaginacionUtil.contiene(cb, cliente.get("nombres"), texto),
                        PaginacionUtil.contiene(cb, cliente.get("apellidos"), texto),
                        PaginacionUtil.contiene(cb, medico.get("nombres"), texto),
                        PaginacionUtil.contiene(cb, medico.get("apellidos"), texto),
                        PaginacionUtil.contiene(cb, medicamento.get("nombre"), texto)));
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
            if (f.getMedicamentoId() != null) {
                filtros.add(cb.equal(medicamento.get("id"), f.getMedicamentoId()));
            }
            if (f.getActivo() != null) {
                filtros.add(cb.equal(root.get("activo"), f.getActivo()));
            }
            if (f.getDesde() != null) {
                filtros.add(cb.greaterThanOrEqualTo(root.get("fechaEmision"), f.getDesde().atStartOfDay()));
            }
            if (f.getHasta() != null) {
                filtros.add(cb.lessThan(root.get("fechaEmision"), f.getHasta().plusDays(1).atStartOfDay()));
            }
            return cb.and(filtros.toArray(new Predicate[0]));
        };
        Page<FormulaResumenRs> pagina = formulaMedicaRepository.findAll(spec, pageable).map(FormulaResumenRs::de);
        return PageRs.de(pagina);
    }

    @Override
    public FormulaMedica buscarFormulaPorId(Long id) {
        if (id == null || id <= 0) {
            throw new BadRequestException("El id de la fórmula debe ser un número mayor a cero.");
        }
        return formulaMedicaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("No existe una fórmula con el id " + id + "."));
    }

    @Override
    @Transactional
    public MiRespuestaRS guardarFormula(FormulaRq rq) throws BadRequestException {
        validarCampos(rq, true);
        // Regla 1: la mascota debe existir.
        Mascota mascota = mascotaRepository.findById(rq.getMascotaId())
                .orElseThrow(() -> new NotFoundException("La mascota no existe."));
        // Regla 2: la mascota debe pertenecer a un cliente registrado.
        if (mascota.getCliente() == null) {
            throw new BadRequestException("La mascota no pertenece a un cliente registrado.");
        }
        // Regla 3: el veterinario debe existir (y estar activo).
        Medico medico = obtenerMedicoActivo(rq.getMedicoId());
        Cita cita = citaRepository.findById(rq.getCitaId())
                .orElseThrow(() -> new NotFoundException("La consulta (cita) no existe."));
        // Regla 7: la consulta debe ser de la misma mascota.
        if (!cita.getMascota().getMascotaId().equals(mascota.getMascotaId())) {
            throw new BadRequestException("La consulta seleccionada pertenece a otra mascota.");
        }
        if (EstadoCita.CANCELADA.equals(cita.getEstado())) {
            throw new ConflictException("No se puede emitir una fórmula sobre una cita cancelada.");
        }
        LocalDateTime fechaEmision = rq.getFechaEmision() != null ? rq.getFechaEmision() : LocalDateTime.now();
        validarFechaEmision(fechaEmision, cita);
        Medicamento medicamento = obtenerMedicamentoRecetable(rq.getMedicamentoId(), fechaEmision);
        validarDosisYDuracion(rq);

        FormulaMedica formula = new FormulaMedica();
        formula.setCita(cita);
        formula.setMascota(mascota);
        formula.setMedico(medico);
        formula.setHistoria(historiaMedicaRepository.findByCita_Id(cita.getId()).orElse(null));
        formula.setMedicamento(medicamento);
        copiarContenido(formula, rq);
        formula.setFechaEmision(fechaEmision);
        formula.setFechaCreacionRegistro(LocalDateTime.now());
        formula.setActivo(Boolean.TRUE);
        formulaMedicaRepository.save(formula);

        return respuesta(201, "Fórmula médica guardada correctamente.");
    }

    @Override
    @Transactional
    public MiRespuestaRS actualizarFormula(FormulaRq rq) throws BadRequestException {
        if (rq == null || rq.getId() == null || rq.getId() <= 0) {
            throw new BadRequestException("El id de la fórmula es obligatorio y debe ser mayor a cero.");
        }
        validarCampos(rq, false);
        FormulaMedica formula = buscarFormulaPorId(rq.getId());
        // Regla 8: una fórmula inactiva ya no se modifica.
        if (Boolean.FALSE.equals(formula.getActivo())) {
            throw new ConflictException("Una fórmula inactiva no puede editarse. Actívala primero.");
        }
        // La consulta, la mascota y el veterinario definen la fórmula emitida: no se cambian.
        if (rq.getCitaId() != null && !rq.getCitaId().equals(formula.getCita().getId())) {
            throw new ConflictException("No se puede cambiar la consulta de una fórmula ya emitida.");
        }
        if (rq.getMascotaId() != null && !rq.getMascotaId().equals(formula.getMascota().getMascotaId())) {
            throw new ConflictException("No se puede cambiar la mascota de una fórmula ya emitida.");
        }
        if (rq.getMedicoId() != null && !rq.getMedicoId().equals(formula.getMedico().getId())) {
            throw new ConflictException("No se puede cambiar el veterinario de una fórmula ya emitida.");
        }
        LocalDateTime fechaEmision = rq.getFechaEmision() != null ? rq.getFechaEmision() : formula.getFechaEmision();
        if (rq.getFechaEmision() != null) {
            validarFechaEmision(fechaEmision, formula.getCita());
        }
        if (!rq.getMedicamentoId().equals(formula.getMedicamento().getId())) {
            formula.setMedicamento(obtenerMedicamentoRecetable(rq.getMedicamentoId(), fechaEmision));
        }
        validarDosisYDuracion(rq);
        copiarContenido(formula, rq);
        formula.setFechaEmision(fechaEmision);
        formula.setFechaActualizacionRegistro(LocalDateTime.now());
        formulaMedicaRepository.save(formula);

        return respuesta(200, "Fórmula médica actualizada correctamente.");
    }

    @Override
    @Transactional
    public MiRespuestaRS cambiarEstadoFormula(Long id, Boolean activo) {
        if (activo == null) {
            throw new BadRequestException("Debes indicar si la fórmula queda activa o inactiva.");
        }
        FormulaMedica formula = buscarFormulaPorId(id);
        if (activo.equals(formula.getActivo())) {
            return respuesta(200, "La fórmula ya estaba " + (activo ? "activa." : "inactiva."));
        }
        formula.setActivo(activo);
        formula.setFechaActualizacionRegistro(LocalDateTime.now());
        formulaMedicaRepository.save(formula);
        return respuesta(200, activo ? "Fórmula activada correctamente." : "Fórmula inactivada correctamente.");
    }

    private Medico obtenerMedicoActivo(Long medicoId) {
        Medico medico = medicoRepository.findById(medicoId)
                .orElseThrow(() -> new NotFoundException("El veterinario no existe."));
        if (Boolean.FALSE.equals(medico.getEstado())) {
            throw new ConflictException("Veterinario no disponible: está inactivo.");
        }
        return medico;
    }

    /** El medicamento debe existir, estar activo y no estar vencido a la fecha de emisión. */
    private Medicamento obtenerMedicamentoRecetable(Long medicamentoId, LocalDateTime fechaEmision) {
        Medicamento medicamento = medicamentoRepository.findById(medicamentoId)
                .orElseThrow(() -> new NotFoundException("El medicamento no existe."));
        if (Boolean.FALSE.equals(medicamento.getActivo())) {
            throw new ConflictException("El medicamento está inactivo y no se puede recetar.");
        }
        if (medicamento.getFechaVence() != null && medicamento.getFechaVence().isBefore(fechaEmision.toLocalDate())) {
            throw new ConflictException("El medicamento está vencido y no se puede recetar.");
        }
        return medicamento;
    }

    /** Regla 4: la emisión no puede ser anterior a la consulta ni estar en el futuro. */
    private void validarFechaEmision(LocalDateTime fechaEmision, Cita cita) {
        if (fechaEmision.isBefore(cita.getFechaHora())) {
            throw new BadRequestException("La fecha de emisión no puede ser anterior a la fecha de la consulta asociada.");
        }
        if (fechaEmision.isAfter(LocalDateTime.now().plusMinutes(TOLERANCIA_MINUTOS))) {
            throw new BadRequestException("La fecha de emisión no puede estar en el futuro.");
        }
    }

    private void copiarContenido(FormulaMedica formula, FormulaRq rq) {
        formula.setDosis(rq.getDosis().trim());
        formula.setFrecuencia(rq.getFrecuencia().trim());
        formula.setDuracion(rq.getDuracion().trim());
        formula.setIndicaciones(rq.getIndicaciones().trim());
    }

    /**
     * Regla 6: validación de campos obligatorios (se repite en el formulario Angular).
     * Al crear se exigen también la mascota y el veterinario.
     */
    private void validarCampos(FormulaRq rq, boolean creando) {
        if (rq == null) {
            throw new BadRequestException("La información de la fórmula es obligatoria.");
        }
        if (creando) {
            if (rq.getMascotaId() == null || rq.getMascotaId() <= 0) {
                throw new BadRequestException("Debes seleccionar la mascota.");
            }
            if (rq.getCitaId() == null || rq.getCitaId() <= 0) {
                throw new BadRequestException("Debes seleccionar la consulta (cita) de la fórmula.");
            }
            if (rq.getMedicoId() == null || rq.getMedicoId() <= 0) {
                throw new BadRequestException("Debes seleccionar el veterinario.");
            }
        }
        if (rq.getMedicamentoId() == null || rq.getMedicamentoId() <= 0) {
            throw new BadRequestException("Debes seleccionar el medicamento.");
        }
        if (esVacio(rq.getDosis())) {
            throw new BadRequestException("La dosis de la fórmula es obligatoria.");
        }
        if (esVacio(rq.getFrecuencia())) {
            throw new BadRequestException("La frecuencia de la fórmula es obligatoria.");
        }
        if (rq.getFrecuencia().trim().length() > LARGO_MAXIMO_FRECUENCIA) {
            throw new BadRequestException("La frecuencia no puede superar los " + LARGO_MAXIMO_FRECUENCIA + " caracteres.");
        }
        if (esVacio(rq.getDuracion())) {
            throw new BadRequestException("La duración de la fórmula es obligatoria.");
        }
        if (esVacio(rq.getIndicaciones())) {
            throw new BadRequestException("Las indicaciones de la fórmula son obligatorias.");
        }
        if (rq.getIndicaciones().trim().length() > LARGO_MAXIMO_INDICACIONES) {
            throw new BadRequestException("Las indicaciones no pueden superar los "
                    + LARGO_MAXIMO_INDICACIONES + " caracteres.");
        }
    }

    /** Regla 5: la dosis y la duración deben ser valores válidos (número mayor a cero con su unidad). */
    private void validarDosisYDuracion(FormulaRq rq) {
        Matcher dosis = PATRON_DOSIS.matcher(rq.getDosis().trim());
        boolean dosisValida = dosis.matches();
        if (dosisValida) {
            double cantidad = Double.parseDouble(dosis.group(1).replace(',', '.'));
            boolean fraccionValida = dosis.group(2) == null || Integer.parseInt(dosis.group(2)) > 0;
            dosisValida = cantidad > 0 && fraccionValida;
        }
        if (!dosisValida) {
            throw new BadRequestException("La dosis debe ser un número mayor a cero con su unidad, "
                    + "por ejemplo: 0.5 ml o 1 tableta.");
        }
        Matcher duracion = PATRON_DURACION.matcher(rq.getDuracion().trim());
        if (!duracion.matches() || Integer.parseInt(duracion.group(1)) <= 0) {
            throw new BadRequestException("La duración debe ser un número mayor a cero con su unidad, "
                    + "por ejemplo: 5 días o 2 semanas.");
        }
        int dias = diasDeDuracion(Integer.parseInt(duracion.group(1)), duracion.group(2));
        if (dias > DURACION_MAXIMA_DIAS) {
            throw new BadRequestException("La duración del tratamiento no puede superar " + DURACION_MAXIMA_DIAS + " días.");
        }
    }

    /** Convierte la duración a días para poder compararla con el máximo permitido. */
    private int diasDeDuracion(int cantidad, String unidad) {
        if (unidad == null) {
            return cantidad;
        }
        String u = unidad.toLowerCase();
        if (u.startsWith("hora")) {
            return (int) Math.ceil(cantidad / 24.0);
        }
        if (u.startsWith("semana")) {
            return cantidad * 7;
        }
        if (u.startsWith("mes")) {
            return cantidad * 30;
        }
        return cantidad;
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
