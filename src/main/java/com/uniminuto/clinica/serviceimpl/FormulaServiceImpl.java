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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class FormulaServiceImpl implements FormulaService {

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

        LocalDateTime ahora = LocalDateTime.now();
        FormulaMedica formula = new FormulaMedica();
        formula.setFechaEmision(ahora.toLocalDate());
        formula.setFechaCreacionRegistro(ahora);

        aplicarDatos(formula, datos, formula.getFechaEmision());
        return formulaMedicaRepository.save(formula);
    }

    @Override
    @Transactional
    public FormulaMedica actualizarFormula(Long id, FormulaMedicaRq datos)
            throws BadRequestException {
        validarDatos(datos);

        FormulaMedica formula = consultarFormula(id);
        LocalDate fechaReferencia = formula.getFechaEmision();

        // Algunas fórmulas antiguas solo tienen fecha de creación.
        if (fechaReferencia == null && formula.getFechaCreacionRegistro() != null) {
            fechaReferencia = formula.getFechaCreacionRegistro().toLocalDate();
        }

        if (fechaReferencia == null) {
            throw new BadRequestException(
                    "La fórmula no tiene una fecha registrada para validar la edición."
            );
        }

        aplicarDatos(formula, datos, fechaReferencia);
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

    private FormulaMedicaRq convertirSolicitud(FormulaRq solicitud)
            throws BadRequestException {
        if (solicitud == null) {
            throw new BadRequestException("Los datos de la fórmula son obligatorios.");
        }

        // Las rutas que reciben FormulaRq requieren indicaciones.
        if (solicitud.getIndicaciones() == null || solicitud.getIndicaciones().isBlank()) {
            throw new BadRequestException("Las indicaciones son obligatorias.");
        }

        FormulaMedicaRq datos = new FormulaMedicaRq();
        datos.setCitaId(solicitud.getCitaId());
        datos.setMedicamentoId(solicitud.getMedicamentoId());
        datos.setDosis(solicitud.getDosis());
        datos.setIndicaciones(solicitud.getIndicaciones());
        datos.setFrecuencia(solicitud.getFrecuencia());
        datos.setDuracionTratamiento(solicitud.getDuracion());
        return datos;
    }

    private void validarDatos(FormulaMedicaRq datos) throws BadRequestException {
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
    }

    private void validarTexto(String valor, String campo, int longitudMaxima)
            throws BadRequestException {
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
    ) throws BadRequestException {
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

        if (medicamento.getFechaVence() == null
                || medicamento.getFechaVence().isBefore(fechaEmision)) {
            throw new BadRequestException(
                    "El medicamento debe estar vigente en la fecha de emisión."
            );
        }

        String indicaciones = datos.getIndicaciones();

        formula.setCita(cita);
        formula.setMedicamento(medicamento);
        formula.setDosis(datos.getDosis().trim());
        formula.setFrecuencia(datos.getFrecuencia().trim());
        formula.setDuracionTratamiento(datos.getDuracionTratamiento().trim());
        formula.setIndicaciones(
                indicaciones == null || indicaciones.isBlank() ? null : indicaciones.trim()
        );
    }

    private MiRespuestaRS respuestaExitosa(String mensaje) {
        MiRespuestaRS respuesta = new MiRespuestaRS();
        respuesta.setStatus(200);
        respuesta.setMessage(mensaje);
        return respuesta;
    }
}