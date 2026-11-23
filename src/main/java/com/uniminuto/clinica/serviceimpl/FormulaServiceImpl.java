package com.uniminuto.clinica.serviceimpl;

import com.uniminuto.clinica.entity.Cita;
import com.uniminuto.clinica.entity.FormulaMedica;
import com.uniminuto.clinica.entity.Medicamento;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.models.FormulaMedicaRq;
import com.uniminuto.clinica.models.FormulaRq;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.repository.CitaRepository;
import com.uniminuto.clinica.repository.FormulaMedicaRepository;
import com.uniminuto.clinica.repository.MedicamentoRepository;
import com.uniminuto.clinica.service.FormulaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.uniminuto.clinica.entity.FormulaMedicaDetalle;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class FormulaServiceImpl implements FormulaService {

    private static final BigDecimal CANTIDAD_MAXIMA =
            new BigDecimal("4294967295");

    private final FormulaMedicaRepository formulaMedicaRepository;
    private final CitaRepository citaRepository;
    private final MedicamentoRepository medicamentoRepository;

    public FormulaServiceImpl(
            FormulaMedicaRepository formulaMedicaRepository,
            CitaRepository citaRepository,
            MedicamentoRepository medicamentoRepository
    ) {
        this.formulaMedicaRepository = formulaMedicaRepository;
        this.citaRepository = citaRepository;
        this.medicamentoRepository = medicamentoRepository;
    }

    @Override
    public List<FormulaMedica> findAllFormulas() {
        return formulaMedicaRepository.findAllByOrderByFechaCreacionRegistroDesc();
    }

    @Override
    public FormulaMedica consultarFormula(Long id) throws BadRequestException {
        if (id == null || id <= 0) {
            throw new BadRequestException("Debes indicar un ID de fórmula válido.");
        }

        return formulaMedicaRepository.findById(id)
                .orElseThrow(() -> new BadRequestException(
                        "No se encontró la fórmula médica."
                ));
    }

    @Override
    @Transactional
    public FormulaMedica crearFormula(FormulaMedicaRq datos)
            throws BadRequestException {
        validarDatos(datos);

        if (datos.getCantidadPrescrita() == null) {
            throw new BadRequestException("La cantidad prescrita es obligatoria.");
        }

        if (datos.getViaAdministracion() == null) {
            throw new BadRequestException("La vía de administración es obligatoria.");
        }

        LocalDateTime ahora = LocalDateTime.now();

        FormulaMedica formula = new FormulaMedica();
        formula.setFechaEmision(ahora.toLocalDate());
        formula.setFechaCreacionRegistro(ahora);

        aplicarDatos(formula, datos, formula.getFechaEmision());
        formula.sincronizarPrimeraLinea();
        return formulaMedicaRepository.save(formula);
    }

    @Override
    @Transactional
    public FormulaMedica actualizarFormula(Long id, FormulaMedicaRq datos)
            throws BadRequestException {
        validarDatos(datos);

        FormulaMedica formula = consultarFormula(id);
        LocalDate fechaReferencia = formula.getFechaEmision();

        if (fechaReferencia == null && formula.getFechaCreacionRegistro() != null) {
            fechaReferencia = formula.getFechaCreacionRegistro().toLocalDate();
        }

        if (fechaReferencia == null) {
            throw new BadRequestException(
                    "La fórmula no tiene una fecha registrada para validar la edición."
            );
        }

        aplicarDatos(formula, datos, fechaReferencia);
        formula.sincronizarPrimeraLinea();
        formula.setFechaActualizacionRegistro(LocalDateTime.now());

        return formulaMedicaRepository.save(formula);
    }

    @Override
    @Transactional
    public MiRespuestaRS guardarFormula(FormulaRq solicitud)
            throws BadRequestException {
        crearFormula(convertirSolicitud(solicitud));
        return respuestaExitosa("Fórmula médica guardada correctamente.");
    }

    @Override
    @Transactional
    public MiRespuestaRS actualizarFormula(FormulaRq solicitud)
            throws BadRequestException {
        FormulaMedicaRq datos = convertirSolicitud(solicitud);
        actualizarFormula(solicitud.getId(), datos);
        return respuestaExitosa("Fórmula médica actualizada correctamente.");
    }

    private FormulaMedicaRq convertirSolicitud(FormulaRq solicitud) {
        if (solicitud == null) {
            throw new BadRequestException("Los datos de la fórmula son obligatorios.");
        }

        if (solicitud.getIndicaciones() == null
                || solicitud.getIndicaciones().isBlank()) {
            throw new BadRequestException("Las indicaciones son obligatorias.");
        }

        FormulaMedicaRq datos = new FormulaMedicaRq();
        datos.setId(solicitud.getId());
        datos.setCitaId(solicitud.getCitaId());
        datos.setMedicamentoId(solicitud.getMedicamentoId());
        datos.setDosis(solicitud.getDosis());
        datos.setIndicaciones(solicitud.getIndicaciones());
        datos.setFrecuencia(solicitud.getFrecuencia());
        datos.setDuracionTratamiento(solicitud.getDuracion());
        datos.setCantidadPrescrita(solicitud.getCantidadPrescrita());
        datos.setViaAdministracion(solicitud.getViaAdministracion());
        return datos;
    }

    private void validarDatos(FormulaMedicaRq datos) {
        if (datos == null) {
            throw new BadRequestException("Los datos de la fórmula son obligatorios.");
        }

        if (datos.getCitaId() == null || datos.getCitaId() <= 0) {
            throw new BadRequestException("Selecciona una cita válida.");
        }

        if (datos.getMedicamentoId() == null || datos.getMedicamentoId() <= 0) {
            throw new BadRequestException("Selecciona un medicamento válido.");
        }

        validarTexto(datos.getDosis(), "La dosis", 5000);
        validarTexto(datos.getFrecuencia(), "La frecuencia", 100);
        validarTexto(datos.getDuracionTratamiento(), "La duración del tratamiento", 100);

        if (datos.getIndicaciones() != null && datos.getIndicaciones().length() > 5000) {
            throw new BadRequestException(
                    "Las indicaciones no pueden superar los 5000 caracteres."
            );
        }

        BigDecimal cantidad = datos.getCantidadPrescrita();
        if (cantidad != null && (
                cantidad.signum() <= 0
                        || cantidad.compareTo(CANTIDAD_MAXIMA) > 0
                        || cantidad.stripTrailingZeros().scale() > 0
        )) {
            throw new BadRequestException(
                    "La cantidad prescrita debe ser un número entero entre 1 y 4294967295."
            );
        }

        if (datos.getViaAdministracion() != null) {
            validarTexto(
                    datos.getViaAdministracion().trim(),
                    "La vía de administración",
                    100
            );
        }
    }

    private void validarTexto(String valor, String campo, int longitudMaxima) {
        if (valor == null || valor.isBlank()) {
            throw new BadRequestException(campo + " es obligatoria.");
        }

        if (valor.length() > longitudMaxima) {
            throw new BadRequestException(
                    campo + " no puede superar los " + longitudMaxima + " caracteres."
            );
        }
    }

    private void aplicarDatos(
            FormulaMedica formula,
            FormulaMedicaRq datos,
            LocalDate fechaEmision
    ) {
        Cita cita = citaRepository.findById(datos.getCitaId())
                .orElseThrow(() -> new BadRequestException(
                        "La cita seleccionada no existe."
                ));

        Medicamento medicamento = medicamentoRepository.findById(datos.getMedicamentoId())
                .orElseThrow(() -> new BadRequestException(
                        "El medicamento seleccionado no existe."
                ));

        if (cita.getCliente() == null
                || cita.getMascota() == null
                || cita.getMascota().getCliente() == null
                || !cita.getCliente().getId().equals(cita.getMascota().getCliente().getId())) {
            throw new BadRequestException(
                    "La mascota no pertenece al cliente de la cita."
            );
        }

        if (cita.getFechaHora() == null
                || fechaEmision.isBefore(cita.getFechaHora().toLocalDate())) {
            throw new BadRequestException(
                    "La fecha de emisión no puede ser anterior a la cita."
            );
        }

        boolean cambiaMedicamento = formula.getMedicamento() == null
                || !medicamento.getId().equals(formula.getMedicamento().getId());

        if (cambiaMedicamento) {
            if (medicamento.getTipoProducto() != Medicamento.TipoProducto.MEDICAMENTO) {
                throw new BadRequestException("Selecciona un medicamento del inventario.");
            }
            if (medicamentoRepository.tieneLoteVigenteParaFormula(medicamento.getId()) == 0) {
                throw new BadRequestException(
                        "El medicamento seleccionado no tiene unidades en lotes vigentes."
                );
            }
        }

        formula.setCita(cita);
        formula.setMedicamento(medicamento);
        formula.setDosis(datos.getDosis().trim());
        formula.setFrecuencia(datos.getFrecuencia().trim());
        formula.setDuracionTratamiento(datos.getDuracionTratamiento().trim());

        String indicaciones = datos.getIndicaciones();
        formula.setIndicaciones(
                indicaciones == null || indicaciones.isBlank() ? null : indicaciones.trim()
        );

        if (datos.getCantidadPrescrita() != null) {
            formula.setCantidadPrescrita(datos.getCantidadPrescrita().longValueExact());
        }

        if (datos.getViaAdministracion() != null) {
            formula.setViaAdministracion(datos.getViaAdministracion().trim());
        }
    }
    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public FormulaMedica agregarMedicamento(
            Long formulaId,
            Long medicoId,
            FormulaMedicaRq datos
    ) {
        if (formulaId == null || formulaId <= 0 || datos == null) {
            throw new BadRequestException("Indica la fórmula y el medicamento.");
        }

        // Las adiciones simultáneas a una fórmula deben asignar sus líneas por turno.
        List<FormulaMedica> encontradas = entityManager.createQuery("""
            SELECT f FROM FormulaMedica f
            WHERE f.id = :formulaId AND f.cita.medico.id = :medicoId
            """, FormulaMedica.class)
                .setParameter("formulaId", formulaId)
                .setParameter("medicoId", medicoId)
                .setLockMode(LockModeType.PESSIMISTIC_WRITE)
                .getResultList();

        if (encontradas.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "La fórmula no pertenece a una cita de tu agenda."
            );
        }

        FormulaMedica formula = encontradas.get(0);

        // La cita se obtiene de la fórmula guardada.
        datos.setCitaId(formula.getCita().getId());
        validarDatos(datos);

        if (datos.getCantidadPrescrita() == null) {
            throw new BadRequestException("La cantidad prescrita es obligatoria.");
        }

        if (datos.getViaAdministracion() == null) {
            throw new BadRequestException("La vía de administración es obligatoria.");
        }

        if (formula.getDetalles().isEmpty()) {
            formula.sincronizarPrimeraLinea();
        }

        // Evita duplicar la misma línea si se reenvía el formulario.
        boolean yaRegistrada = formula.getDetalles().stream().anyMatch(linea ->
                linea.getMedicamento().getId().equals(datos.getMedicamentoId())
                        && java.util.Objects.equals(linea.getDosis(), datos.getDosis().trim())
                        && java.util.Objects.equals(linea.getFrecuencia(), datos.getFrecuencia().trim())
                        && java.util.Objects.equals(
                        linea.getDuracionTratamiento(), datos.getDuracionTratamiento().trim())
                        && java.util.Objects.equals(
                        linea.getCantidadPrescrita(), datos.getCantidadPrescrita().longValueExact())
                        && java.util.Objects.equals(
                        linea.getViaAdministracion(), datos.getViaAdministracion().trim())
                        && java.util.Objects.equals(
                        linea.getIndicaciones(),
                        datos.getIndicaciones() == null || datos.getIndicaciones().isBlank()
                                ? null : datos.getIndicaciones().trim())
        );

        if (yaRegistrada) {
            throw new BadRequestException("Esta línea ya está registrada en la fórmula.");
        }

        // Reutiliza la validación del medicamento y sus lotes vigentes.
        FormulaMedica temporal = new FormulaMedica();
        LocalDate fechaReferencia = formula.getFechaEmision();

        if (fechaReferencia == null && formula.getFechaCreacionRegistro() != null) {
            fechaReferencia = formula.getFechaCreacionRegistro().toLocalDate();
        }

        if (fechaReferencia == null) {
            throw new BadRequestException("La fórmula no tiene fecha registrada.");
        }

        aplicarDatos(temporal, datos, fechaReferencia);

        FormulaMedicaDetalle linea = new FormulaMedicaDetalle();
        linea.setMedicamento(temporal.getMedicamento());
        linea.setDosis(temporal.getDosis());
        linea.setFrecuencia(temporal.getFrecuencia());
        linea.setDuracionTratamiento(temporal.getDuracionTratamiento());
        linea.setCantidadPrescrita(temporal.getCantidadPrescrita());
        linea.setViaAdministracion(temporal.getViaAdministracion());
        linea.setIndicaciones(temporal.getIndicaciones());

        formula.getDetalles().add(linea);
        formula.setFechaActualizacionRegistro(LocalDateTime.now());

        return formulaMedicaRepository.saveAndFlush(formula);
    }

    private MiRespuestaRS respuestaExitosa(String mensaje) {
        MiRespuestaRS respuesta = new MiRespuestaRS();
        respuesta.setStatus(200);
        respuesta.setMessage(mensaje);
        return respuesta;
    }
}