package com.uniminuto.clinica.serviceimpl;

import com.uniminuto.clinica.entity.Cita;
import com.uniminuto.clinica.entity.FormulaMedica;
import com.uniminuto.clinica.models.FormulaMedicaRq;
import com.uniminuto.clinica.service.FormulaService;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.models.AnotacionHistoriaRq;
import com.uniminuto.clinica.service.AnotacionHistoriaService;
import com.uniminuto.clinica.service.CitaService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.TypedQuery;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

@Service
@Transactional(readOnly = true)
public class AgendaCitasServiceImpl {

    private final JdbcTemplate jdbc;
    private final ServiciosAtencionService servicios;
    private final EntityManager entityManager;
    private final CitaService citaService;
    private final AnotacionHistoriaService anotacionHistoriaService;

    private final FormulaService formulas;

    public AgendaCitasServiceImpl(
            JdbcTemplate jdbc,
            ServiciosAtencionService servicios,
            EntityManager entityManager,
            CitaService citaService,
            AnotacionHistoriaService anotacionHistoriaService, FormulaService formulas
    ) {
        this.jdbc = jdbc;
        this.servicios = servicios;
        this.formulas = formulas;
        this.entityManager = entityManager;
        this.citaService = citaService;
        this.anotacionHistoriaService = anotacionHistoriaService;
    }

    private record Acceso(String perfil, Long medicoId) {
    }

    public record DatosAtencion(
            String sintomas,
            String diagnostico,
            String observaciones,
            String recomendaciones,
            FormulaMedicaRq formula,
            List<Long> serviciosAdicionales
    ) {
    }

    public List<Cita> listar(String autorizacion) {
        Acceso acceso = resolverAcceso(autorizacion);

        if (acceso.medicoId() == null) {
            return citaService.listarCitas();
        }

        return consultaDelMedico(acceso.medicoId(), "")
                .getResultList();
    }

    public List<Cita> listarRango(
            String autorizacion,
            LocalDateTime inicio,
            LocalDateTime fin
    ) {
        Acceso acceso = resolverAcceso(autorizacion);

        if (acceso.medicoId() == null) {
            return citaService.listarCitasRango(inicio, fin);
        }

        if (inicio == null || fin == null || inicio.isAfter(fin)) {
            throw new BadRequestException("Revisa el rango de fechas.");
        }

        return consultaDelMedico(
                acceso.medicoId(),
                " AND c.fechaHora BETWEEN :inicio AND :fin"
        )
                .setParameter("inicio", inicio)
                .setParameter("fin", fin)
                .getResultList();
    }

    public List<Cita> listarPorMascota(
            String autorizacion,
            Integer mascotaId
    ) {
        Acceso acceso = resolverAcceso(autorizacion);

        if (acceso.medicoId() == null) {
            return citaService.listarCitasPorMascota(mascotaId);
        }

        if (mascotaId == null || mascotaId <= 0) {
            throw new BadRequestException("Selecciona una mascota válida.");
        }

        return consultaDelMedico(
                acceso.medicoId(),
                " AND c.mascota.mascotaId = :mascotaId"
        )
                .setParameter("mascotaId", mascotaId)
                .getResultList();
    }

    private TypedQuery<Cita> consultaDelMedico(Long medicoId, String filtro) {
        String consulta = "SELECT c FROM Cita c WHERE c.medico.id = :medicoId"
                + filtro
                + " ORDER BY c.fechaHora DESC, c.id DESC";

        return entityManager.createQuery(consulta, Cita.class)
                .setParameter("medicoId", medicoId);
    }

    @Transactional
    public FormulaMedica finalizarAtencion(
            String autorizacion,
            Long citaId,
            DatosAtencion datos
    ) {
        Acceso acceso = resolverAcceso(autorizacion);

        if (acceso.medicoId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "La atención debe finalizarla el veterinario asignado."
            );
        }

        if (datos == null) {
            throw new BadRequestException("Completa los datos de la atención.");
        }

        String sintomas = textoObligatorio(datos.sintomas(), "los síntomas");
        String diagnostico = textoObligatorio(datos.diagnostico(), "el diagnóstico");
        String observaciones = textoObligatorio(datos.observaciones(), "las observaciones");
        String recomendaciones = textoObligatorio(datos.recomendaciones(), "las recomendaciones");

        // Dos solicitudes de finalización deben consultar esta cita por turno.
        List<Cita> citas = entityManager.createQuery("""
                SELECT c FROM Cita c
                WHERE c.id = :citaId AND c.medico.id = :medicoId
                """, Cita.class)
                .setParameter("citaId", citaId)
                .setParameter("medicoId", acceso.medicoId())
                .setLockMode(LockModeType.PESSIMISTIC_WRITE)
                .getResultList();

        if (citas.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "La cita no aparece en tu agenda."
            );
        }

        Cita cita = citas.get(0);
        String estado = cita.getEstado() == null
                ? ""
                : cita.getEstado().trim().toUpperCase(Locale.ROOT);

        if (!List.of("PROGRAMADA", "PENDIENTE", "CONFIRMADA").contains(estado)) {
            throw new BadRequestException(
                    "Solo puedes finalizar una cita programada, pendiente o confirmada."
            );
        }

        if (cita.getFechaHora() == null || cita.getFechaHora().isAfter(LocalDateTime.now())) {
            throw new BadRequestException("Aún no ha llegado la hora de esta cita.");
        }

        AnotacionHistoriaRq nota = new AnotacionHistoriaRq();
        nota.setCitaId(cita.getId());
        nota.setMedicoId(acceso.medicoId());
        nota.setDescripcion("Síntomas: " + sintomas);
        nota.setDiagnostico(diagnostico);
        nota.setObservaciones(
                "Observaciones: " + observaciones
                        + "\nRecomendaciones: " + recomendaciones
        );

        anotacionHistoriaService.crearAnotacionHistoria(nota);
        servicios.guardar(citaId, acceso.medicoId(), datos.serviciosAdicionales());
        cita.setEstado("ATENDIDA");

        if (datos.formula() == null) return null;
        datos.formula().setCitaId(cita.getId());
        return formulas.crearFormula(datos.formula());
    }

    public List<ServiciosAtencionService.Servicio> consultarServicios(String autorizacion, Long citaId) {
        return servicios.disponibles(citaId, obtenerMedicoDeSesion(autorizacion));
    }

    public record Antecedente(
            Long id, LocalDateTime fecha, String descripcion,
            String diagnostico, String observaciones
    ) {}

    @Transactional(readOnly = true)
    public List<Antecedente> consultarAntecedentes(String autorizacion, Long citaId) {
        Long medicoId = obtenerMedicoDeSesion(autorizacion);
        List<Long> pacientes = jdbc.queryForList("""
                SELECT mascota_id FROM cita WHERE id = ? AND medico_id = ?
                """, Long.class, citaId, medicoId);

        if (pacientes.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "La cita no aparece en tu agenda."
            );
        }

        return jdbc.query("""
                SELECT a.id, a.fecha, a.descripcion, a.diagnostico, a.observaciones
                FROM anotacion_historia a
                JOIN historia_medica h ON h.id = a.historia_id
                WHERE h.paciente_id = ?
                ORDER BY a.fecha DESC, a.id DESC
                """, (rs, fila) -> new Antecedente(
                rs.getLong("id"),
                rs.getObject("fecha", LocalDateTime.class),
                rs.getString("descripcion"),
                rs.getString("diagnostico"),
                rs.getString("observaciones")
        ), pacientes.get(0));
    }

    private String textoObligatorio(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new BadRequestException("Debes registrar " + campo + ".");
        }
        return valor.trim();
    }
    public Long obtenerMedicoDeSesion(String autorizacion) {
        Acceso acceso = resolverAcceso(autorizacion);

        if (acceso.medicoId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Esta operación requiere una cuenta de veterinario activa."
            );
        }

        return acceso.medicoId();
    }

    private Acceso resolverAcceso(String autorizacion) {
        if (autorizacion == null || !autorizacion.startsWith("Bearer ")) {
            throw sesionInvalida();
        }

        String token = autorizacion.substring(7).trim();
        if (token.isEmpty()) {
            throw sesionInvalida();
        }

        List<Acceso> resultados = jdbc.query("""
                SELECT p.nombre AS perfil, m.id AS medico_id
                FROM `session` s
                JOIN usuario u ON u.id = s.user_id
                JOIN perfil p ON p.perfil_id = u.rol
                LEFT JOIN medico m ON m.usuario_id = u.id AND m.estado = 1
                WHERE BINARY s.token = BINARY ?
                  AND s.fecha_expiracion > CURRENT_TIMESTAMP
                  AND u.activo = 1
                  AND p.activo = 1
                ORDER BY s.session_id DESC
                LIMIT 1
                """,
                (rs, fila) -> new Acceso(
                        rs.getString("perfil"),
                        rs.getObject("medico_id", Long.class)
                ),
                token
        );

        if (resultados.isEmpty()) {
            throw sesionInvalida();
        }

        Acceso acceso = resultados.get(0);
        String perfil = acceso.perfil().trim().toLowerCase(Locale.ROOT);

        return switch (perfil) {
            case "administrador", "recepcion", "recepción" ->
                    new Acceso(acceso.perfil(), null);

            case "medico", "médico" -> {
                if (acceso.medicoId() == null) {
                    throw new ResponseStatusException(
                            HttpStatus.FORBIDDEN,
                            "Esta cuenta no tiene un veterinario activo vinculado."
                    );
                }
                yield acceso;
            }

            default -> throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Tu perfil no tiene acceso a la agenda médica."
            );
        };
    }

    private ResponseStatusException sesionInvalida() {
        return new ResponseStatusException(
                HttpStatus.UNAUTHORIZED,
                "Tu sesión no está vigente. Vuelve a iniciar sesión."
        );
    }
}