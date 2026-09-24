package main.java.com.uniminuto.clinica.service;

import com.uniminuto.clinica.models.Cita;
import java.time.LocalDate;
import java.util.List;

/**
 * Interfaz que define los servicios de negocio para la gestión de citas veterinarias.
 */
public interface CitaService {

    /**
     * Registra una nueva cita aplicando validaciones de horario y estado.
     */
    Cita crearCita(Cita cita);

    /**
     * Actualiza la información de una cita existente validando su estado activo.
     */
    Cita actualizarCita(Long id, Cita citaActualizada);

    /**
     * Consulta y filtra las citas en un rango de fechas determinado.
     */
    List<Cita> filtrarCitas(LocalDate inicio, LocalDate fin);
}
