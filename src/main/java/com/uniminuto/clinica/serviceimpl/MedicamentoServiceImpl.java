package com.uniminuto.clinica.serviceimpl;

import com.uniminuto.clinica.entity.Medicamento;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.exception.ConflictException;
import com.uniminuto.clinica.exception.NotFoundException;
import com.uniminuto.clinica.models.MedicamentoRq;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.repository.MedicamentoRepository;
import com.uniminuto.clinica.service.MedicamentoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Implementación de las reglas de negocio de medicamentos.
 * <p>
 * Un medicamento nunca se borra: las fórmulas ya emitidas lo referencian y
 * deben seguir mostrándolo. Para dejar de usarlo se desactiva.
 */
@Service
public class MedicamentoServiceImpl implements MedicamentoService {

    private static final int LARGO_MAXIMO_NOMBRE = 100;
    private static final int LARGO_MAXIMO_PRESENTACION = 100;
    private static final int LARGO_MAXIMO_DESCRIPCION = 1000;

    private final MedicamentoRepository medicamentoRepository;

    /**
     * Crea el servicio con sus dependencias.
     *
     * @param medicamentoRepository repositorio de medicamentos.
     */
    public MedicamentoServiceImpl(MedicamentoRepository medicamentoRepository) {
        this.medicamentoRepository = medicamentoRepository;
    }

    @Override
    public List<Medicamento> listarMedicamentos() throws BadRequestException {
        return medicamentoRepository.findAll().stream()
                .sorted((m1, m2) -> m1.getNombre().compareToIgnoreCase(m2.getNombre()))
                .toList();
    }

    @Override
    public List<Medicamento> listarMedicamentosActivos() {
        return medicamentoRepository.findByActivoOrderByNombreAsc(Boolean.TRUE);
    }

    @Override
    public Medicamento buscarMedicamentoPorId(Long id) {
        if (id == null || id <= 0) {
            throw new BadRequestException("El id del medicamento debe ser un número mayor a cero.");
        }
        return medicamentoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("No existe un medicamento con el id " + id + "."));
    }

    @Override
    @Transactional
    public MiRespuestaRS crearMedicamento(MedicamentoRq rq) {
        validar(rq);
        String nombre = rq.getNombre().trim();
        // Regla: no se repite el nombre (sin importar mayúsculas) para no duplicar el inventario.
        if (medicamentoRepository.existsByNombreIgnoreCase(nombre)) {
            throw new ConflictException("Ya existe un medicamento llamado \"" + nombre + "\".");
        }
        // Regla: no se registra un lote que ya llega vencido; sería un medicamento imposible de recetar.
        if (rq.getFechaVence().isBefore(LocalDate.now())) {
            throw new BadRequestException("No se puede registrar un medicamento que ya está vencido.");
        }
        Medicamento medicamento = new Medicamento();
        mapear(medicamento, rq);
        medicamento.setActivo(Boolean.TRUE);
        medicamento.setFechaCreacionRegistro(LocalDateTime.now());
        medicamentoRepository.save(medicamento);
        return respuesta(201, "Medicamento creado correctamente.");
    }

    @Override
    @Transactional
    public MiRespuestaRS actualizarMedicamento(Long id, MedicamentoRq rq) {
        Medicamento medicamento = buscarMedicamentoPorId(id);
        validar(rq);
        String nombre = rq.getNombre().trim();
        if (medicamentoRepository.existsByNombreIgnoreCaseAndIdNot(nombre, id)) {
            throw new ConflictException("Otro medicamento ya se llama \"" + nombre + "\".");
        }
        // Solo se exige fecha futura si el usuario cambió la fecha de vencimiento; así se puede
        // corregir la descripción de un medicamento que ya venció sin que el sistema lo impida.
        boolean cambioVencimiento = !rq.getFechaVence().equals(medicamento.getFechaVence());
        if (cambioVencimiento && rq.getFechaVence().isBefore(LocalDate.now())) {
            throw new BadRequestException("La nueva fecha de vencimiento no puede estar en el pasado.");
        }
        mapear(medicamento, rq);
        medicamento.setFechaModificacionRegistro(LocalDateTime.now());
        medicamentoRepository.save(medicamento);
        return respuesta(200, "Medicamento actualizado correctamente.");
    }

    @Override
    @Transactional
    public MiRespuestaRS cambiarEstadoMedicamento(Long id, Boolean activo) {
        if (activo == null) {
            throw new BadRequestException("Debes indicar si el medicamento queda activo o inactivo.");
        }
        Medicamento medicamento = buscarMedicamentoPorId(id);
        if (activo.equals(medicamento.getActivo())) {
            return respuesta(200, "El medicamento ya estaba " + (activo ? "activo." : "inactivo."));
        }
        // Regla: un medicamento vencido no puede volver a estar disponible para recetar.
        if (activo && medicamento.getFechaVence().isBefore(LocalDate.now())) {
            throw new ConflictException("No se puede activar el medicamento porque venció el "
                    + medicamento.getFechaVence() + ". Actualiza la fecha de vencimiento si llegó un lote nuevo.");
        }
        medicamento.setActivo(activo);
        medicamento.setFechaModificacionRegistro(LocalDateTime.now());
        medicamentoRepository.save(medicamento);
        return respuesta(200, activo ? "Medicamento activado correctamente." : "Medicamento desactivado correctamente.");
    }

    /** Valida los datos de entrada del medicamento antes de guardar. */
    private void validar(MedicamentoRq rq) {
        if (rq == null) {
            throw new BadRequestException("La información del medicamento es obligatoria.");
        }
        String nombre = limpiar(rq.getNombre());
        if (nombre == null) {
            throw new BadRequestException("El nombre del medicamento es obligatorio.");
        }
        if (nombre.length() > LARGO_MAXIMO_NOMBRE) {
            throw new BadRequestException("El nombre no puede superar " + LARGO_MAXIMO_NOMBRE + " caracteres.");
        }
        String presentacion = limpiar(rq.getPresentacion());
        if (presentacion != null && presentacion.length() > LARGO_MAXIMO_PRESENTACION) {
            throw new BadRequestException(
                    "La presentación no puede superar " + LARGO_MAXIMO_PRESENTACION + " caracteres.");
        }
        String descripcion = limpiar(rq.getDescripcion());
        if (descripcion != null && descripcion.length() > LARGO_MAXIMO_DESCRIPCION) {
            throw new BadRequestException(
                    "La descripción no puede superar " + LARGO_MAXIMO_DESCRIPCION + " caracteres.");
        }
        if (rq.getFechaCompra() == null) {
            throw new BadRequestException("La fecha de compra es obligatoria.");
        }
        if (rq.getFechaVence() == null) {
            throw new BadRequestException("La fecha de vencimiento es obligatoria.");
        }
        if (rq.getFechaCompra().isAfter(LocalDate.now())) {
            throw new BadRequestException("La fecha de compra no puede ser futura.");
        }
        // Regla: un lote no puede vencer el mismo día (ni antes) de haber sido comprado.
        if (!rq.getFechaVence().isAfter(rq.getFechaCompra())) {
            throw new BadRequestException("La fecha de vencimiento debe ser posterior a la fecha de compra.");
        }
    }

    /** Copia los datos validados del Rq a la entidad, limpiando espacios sobrantes. */
    private void mapear(Medicamento medicamento, MedicamentoRq rq) {
        medicamento.setNombre(limpiar(rq.getNombre()));
        medicamento.setPresentacion(limpiar(rq.getPresentacion()));
        medicamento.setDescripcion(limpiar(rq.getDescripcion()));
        medicamento.setFechaCompra(rq.getFechaCompra());
        medicamento.setFechaVence(rq.getFechaVence());
    }

    /** Quita espacios al inicio y al final; devuelve {@code null} si queda vacío. */
    private String limpiar(String valor) {
        if (valor == null) {
            return null;
        }
        String recortado = valor.trim();
        return recortado.isEmpty() ? null : recortado;
    }

    private MiRespuestaRS respuesta(int status, String mensaje) {
        MiRespuestaRS rta = new MiRespuestaRS();
        rta.setStatus(status);
        rta.setMessage(mensaje);
        return rta;
    }
}
