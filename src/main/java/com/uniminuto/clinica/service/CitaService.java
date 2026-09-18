package com.uniminuto.clinica.service;

import com.uniminuto.clinica.entity.Cita;
import com.uniminuto.clinica.exception.BadRequestException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Interfaz de servicio para la gestion de citas medicas.
 */
public interface CitaService {

    List<Cita> listarCitas() throws BadRequestException;

    /**
     * Filtra las citas en un rango de fechas ordenadas de la mas reciente a la mas antigua.
     */
    List<Cita> filtrarCitasPorFecha(LocalDateTime fechaInicio, LocalDateTime fechaFin) throws BadRequestException;

    /**
     * Crea una nueva cita en el sistema validando sus datos obligatorios.
     */
    Cita crearCita(Cita cita) throws BadRequestException;

    /**
     * Actualiza los datos de una cita existente identificada por su ID.
     */
    Cita actualizarCita(Long id, Cita cita) throws BadRequestException;
}