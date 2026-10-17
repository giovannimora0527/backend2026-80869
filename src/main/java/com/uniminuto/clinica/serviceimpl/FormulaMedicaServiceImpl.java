package com.uniminuto.clinica.serviceimpl;

import com.uniminuto.clinica.entity.Cita;
import com.uniminuto.clinica.entity.FormulaMedica;
import com.uniminuto.clinica.entity.Medicamento;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.exception.ConflictException;
import com.uniminuto.clinica.exception.ResourceNotFoundException;
import com.uniminuto.clinica.models.FormulaMedicaRq;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.repository.CitaRepository;
import com.uniminuto.clinica.repository.FormulaMedicaRepository;
import com.uniminuto.clinica.repository.MedicamentoRepository;
import com.uniminuto.clinica.service.FormulaMedicaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Implementación de la lógica de negocio para la gestión de Fórmulas Médicas.
 * Maneja validaciones de estado de citas, existencia de medicamentos y control de errores.
 *
 * @author Yulian Estefanía Montealegre Rivas
 */
@Service
public class FormulaMedicaServiceImpl implements FormulaMedicaService {

    @Autowired
    private FormulaMedicaRepository formulaMedicaRepository;

    @Autowired
    private CitaRepository citaRepository;

    @Autowired
    private MedicamentoRepository medicamentoRepository;

    /**
     * Obtiene el listado completo de fórmulas médicas ordenadas descendentemente.
     *
     * @return Lista de fórmulas médicas.
     */
    @Override
    public List<FormulaMedica> obtenerTodas() {
        return formulaMedicaRepository.findAllByOrderByFechaCreacionRegistroDesc();
    }

    /**
     * Registra una nueva fórmula médica validando la existencia de la cita,
     * que la cita no esté cancelada y que el medicamento exista.
     *
     * @param request Datos de la fórmula a registrar.
     * @return Respuesta estandarizada con estado 200 si es exitoso.
     */
    @Override
    public MiRespuestaRS crearFormula(FormulaMedicaRq request) {
        this.validarFormulaRq(request);

        // 1. Validar existencia de la cita
        Cita cita = citaRepository.findById(request.getCitaId())
                .orElseThrow(() -> new ResourceNotFoundException("La cita médica con ID " + request.getCitaId() + " no fue encontrada."));

        // 2. Regla de negocio: No se pueden emitir fórmulas para citas canceladas
        if ("CANCELADA".equalsIgnoreCase(cita.getEstado())) {
            throw new ConflictException("No se puede emitir una fórmula médica para una cita cancelada.");
        }

        // 3. Validar existencia del medicamento
        Medicamento medicamento = medicamentoRepository.findById(request.getMedicamentoId())
                .orElseThrow(() -> new ResourceNotFoundException("El medicamento con ID " + request.getMedicamentoId() + " no fue encontrado."));

        // 4. Instanciar entidad y asignar campos
        FormulaMedica formula = new FormulaMedica();
        formula.setCitaId(cita.getId());
        formula.setMedicamentoId(medicamento.getId());

        // Autocompletar datos por defecto si vienen vacíos
        formula.setNombreMedicamento(request.getNombreMedicamento() != null && !request.getNombreMedicamento().trim().isEmpty()
                ? request.getNombreMedicamento() : medicamento.getNombre());
        formula.setPresentacion(request.getPresentacion() != null && !request.getPresentacion().trim().isEmpty()
                ? request.getPresentacion() : medicamento.getPresentacion());
        formula.setDescripcion(request.getDescripcion() != null && !request.getDescripcion().trim().isEmpty()
                ? request.getDescripcion() : medicamento.getDescripcion());

        formula.setDosis(request.getDosis());
        formula.setIndicaciones(request.getIndicaciones());
        formula.setFechaCreacionRegistro(LocalDateTime.now());
        formula.setFechaActualizacionRegistro(LocalDateTime.now());

        formulaMedicaRepository.save(formula);

        MiRespuestaRS response = new MiRespuestaRS();
        response.setStatus(200);
        response.setMessage("Fórmula médica prescrita exitosamente para la cita ID " + cita.getId());
        return response;
    }

    /**
     * Actualiza una fórmula médica previamente registrada.
     *
     * @param id Identificador de la fórmula médica.
     * @param request Datos actualizados.
     * @return Respuesta estandarizada.
     */
    @Override
    public MiRespuestaRS actualizarFormula(Long id, FormulaMedicaRq request) {
        this.validarFormulaRq(request);

        FormulaMedica existente = formulaMedicaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("La fórmula médica con ID " + id + " no existe."));

        Cita cita = citaRepository.findById(request.getCitaId())
                .orElseThrow(() -> new ResourceNotFoundException("La cita médica con ID " + request.getCitaId() + " no fue encontrada."));

        if ("CANCELADA".equalsIgnoreCase(cita.getEstado())) {
            throw new ConflictException("No se puede actualizar una fórmula médica asociada a una cita cancelada.");
        }

        Medicamento medicamento = medicamentoRepository.findById(request.getMedicamentoId())
                .orElseThrow(() -> new ResourceNotFoundException("El medicamento con ID " + request.getMedicamentoId() + " no fue encontrado."));

        existente.setCitaId(cita.getId());
        existente.setMedicamentoId(medicamento.getId());
        existente.setNombreMedicamento(request.getNombreMedicamento() != null ? request.getNombreMedicamento() : medicamento.getNombre());
        existente.setPresentacion(request.getPresentacion() != null ? request.getPresentacion() : medicamento.getPresentacion());
        existente.setDescripcion(request.getDescripcion());
        existente.setDosis(request.getDosis());
        existente.setIndicaciones(request.getIndicaciones());
        existente.setFechaActualizacionRegistro(LocalDateTime.now());

        formulaMedicaRepository.save(existente);

        MiRespuestaRS response = new MiRespuestaRS();
        response.setStatus(200);
        response.setMessage("Fórmula médica ID " + id + " actualizada correctamente.");
        return response;
    }

    /**
     * Valida que los campos obligatorios del DTO no sean nulos o vacíos.
     *
     * @param request DTO con la información de la fórmula.
     */
    private void validarFormulaRq(FormulaMedicaRq request) {
        if (request == null) {
            throw new BadRequestException("Los datos de la fórmula médica no pueden ser nulos.");
        }
        if (request.getCitaId() == null) {
            throw new BadRequestException("El ID de la cita es obligatorio.");
        }
        if (request.getMedicamentoId() == null) {
            throw new BadRequestException("El ID del medicamento es obligatorio.");
        }
        if (request.getDosis() == null || request.getDosis().trim().isEmpty()) {
            throw new BadRequestException("La dosis médica es obligatoria.");
        }
    }
}