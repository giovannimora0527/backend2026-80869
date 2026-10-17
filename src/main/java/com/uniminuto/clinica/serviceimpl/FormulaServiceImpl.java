
package com.uniminuto.clinica.serviceimpl;

import com.uniminuto.clinica.entity.Cita;
import com.uniminuto.clinica.entity.FormulaMedica;
import com.uniminuto.clinica.entity.Medicamento;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.models.FormulaMedicaRq;
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
        return formulaMedicaRepository
                .findAllByOrderByFechaCreacionRegistroDesc();
    }

    @Override
    public FormulaMedica consultarFormula(Long id)
            throws BadRequestException {

        if (id == null || id <= 0) {
            throw new BadRequestException(
                    "Debes indicar un identificador válido."
            );
        }

        return formulaMedicaRepository.findById(id)
                .orElseThrow(() -> new BadRequestException(
                        "No se encontró la fórmula médica."
                ));
    }

    @Override
    public FormulaMedica crearFormula(FormulaMedicaRq formulaRq)
            throws BadRequestException {

        validarDatos(formulaRq);

        Cita cita = obtenerCita(formulaRq.getCitaId());
        Medicamento medicamento =
                obtenerMedicamento(formulaRq.getMedicamentoId());

        validarRelacionesYFechas(
                cita,
                medicamento,
                formulaRq.getFechaEmision()
        );

        FormulaMedica formula = new FormulaMedica();

        formula.setCita(cita);
        formula.setMedicamento(medicamento);
        formula.setDosis(formulaRq.getDosis().trim());
        formula.setIndicaciones(limpiarIndicaciones(
                formulaRq.getIndicaciones()
        ));
        formula.setFechaEmision(formulaRq.getFechaEmision());
        formula.setFrecuencia(formulaRq.getFrecuencia().trim());
        formula.setDuracionTratamiento(
                formulaRq.getDuracionTratamiento().trim()
        );
        formula.setFechaCreacionRegistro(LocalDateTime.now());

        return formulaMedicaRepository.save(formula);
    }

    @Override
    @Transactional
    public FormulaMedica actualizarFormula(
            Long id,
            FormulaMedicaRq formulaRq
    ) throws BadRequestException {

        if (id == null || id <= 0) {
            throw new BadRequestException(
                    "Debes indicar un identificador válido."
            );
        }

        validarDatos(formulaRq);

        FormulaMedica formula = formulaMedicaRepository.findById(id)
                .orElseThrow(() -> new BadRequestException(
                        "No se encontró la fórmula médica que deseas editar."
                ));

        Cita cita = obtenerCita(formulaRq.getCitaId());
        Medicamento medicamento =
                obtenerMedicamento(formulaRq.getMedicamentoId());

        validarRelacionesYFechas(
                cita,
                medicamento,
                formulaRq.getFechaEmision()
        );

        formula.setCita(cita);
        formula.setMedicamento(medicamento);
        formula.setDosis(formulaRq.getDosis().trim());
        formula.setIndicaciones(limpiarIndicaciones(
                formulaRq.getIndicaciones()
        ));
        formula.setFechaEmision(formulaRq.getFechaEmision());
        formula.setFrecuencia(formulaRq.getFrecuencia().trim());
        formula.setDuracionTratamiento(
                formulaRq.getDuracionTratamiento().trim()
        );
        formula.setFechaActualizacionRegistro(LocalDateTime.now());

        return formulaMedicaRepository.save(formula);
    }

    private void validarDatos(FormulaMedicaRq formulaRq)
            throws BadRequestException {

        if (formulaRq == null
                || formulaRq.getCitaId() == null
                || formulaRq.getMedicamentoId() == null
                || formulaRq.getDosis() == null
                || formulaRq.getDosis().isBlank()
                || formulaRq.getFechaEmision() == null
                || formulaRq.getFrecuencia() == null
                || formulaRq.getFrecuencia().isBlank()
                || formulaRq.getDuracionTratamiento() == null
                || formulaRq.getDuracionTratamiento().isBlank()) {

            throw new BadRequestException(
                    "Completa la cita, el medicamento, la dosis, "
                            + "la fecha de emisión, la frecuencia "
                            + "y la duración del tratamiento."
            );
        }

        if (formulaRq.getCitaId() <= 0
                || formulaRq.getMedicamentoId() <= 0) {
            throw new BadRequestException(
                    "La cita y el medicamento deben ser válidos."
            );
        }

        if (formulaRq.getDosis().length() > 5000
                || formulaRq.getFrecuencia().length() > 100
                || formulaRq.getDuracionTratamiento().length() > 100) {
            throw new BadRequestException(
                    "Revisa la longitud de la dosis, la frecuencia "
                            + "y la duración del tratamiento."
            );
        }

        if (formulaRq.getIndicaciones() != null
                && formulaRq.getIndicaciones().length() > 5000) {
            throw new BadRequestException(
                    "Las indicaciones no pueden superar los 5000 caracteres."
            );
        }

        if (formulaRq.getFechaEmision().isAfter(LocalDate.now())) {
            throw new BadRequestException(
                    "La fecha de emisión no puede ser futura."
            );
        }
    }

    private Cita obtenerCita(Long citaId)
            throws BadRequestException {

        return citaRepository.findById(citaId)
                .orElseThrow(() -> new BadRequestException(
                        "La cita seleccionada no existe."
                ));
    }

    private Medicamento obtenerMedicamento(Long medicamentoId)
            throws BadRequestException {

        return medicamentoRepository.findById(medicamentoId)
                .orElseThrow(() -> new BadRequestException(
                        "El medicamento seleccionado no existe."
                ));
    }

    private void validarRelacionesYFechas(
            Cita cita,
            Medicamento medicamento,
            LocalDate fechaEmision
    ) throws BadRequestException {

        if (cita.getCliente() == null
                || cita.getMascota() == null
                || cita.getMascota().getCliente() == null
                || !cita.getCliente().getId().equals(
                cita.getMascota().getCliente().getId()
        )) {
            throw new BadRequestException(
                    "La mascota no pertenece al cliente de la cita."
            );
        }

        if (cita.getFechaHora() == null
                || fechaEmision.isBefore(
                cita.getFechaHora().toLocalDate()
        )) {
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
    }

    private String limpiarIndicaciones(String indicaciones) {
        if (indicaciones == null || indicaciones.isBlank()) {
            return null;
        }

        return indicaciones.trim();
    }
}
