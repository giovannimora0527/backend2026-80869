package com.uniminuto.clinica.service;

import com.uniminuto.clinica.entity.Cita;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.models.CitaRq;
import com.uniminuto.clinica.models.MiRespuestaRS;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Interfaz que define los servicios de negocio para la gestión de Citas Médicas.
 *
 * @author Yulian Estefanía Montealegre
 */
public interface CitaService {

    /**
     * Obtiene el listado completo de citas médicas registradas.
     *
     * @return Lista de citas.
     * @throws BadRequestException Si ocurre un error en la solicitud.
     */
    List<Cita> listarCitas() throws BadRequestException;

    /**
     * Obtiene el listado de citas dentro de un rango de fechas.
     *
     * @param fechaInicio Fecha y hora inicial.
     * @param fechaFin Fecha y hora final.
     * @return Lista de citas filtradas.
     * @throws BadRequestException Si los parámetros de fecha son inválidos.
     */
    List<Cita> listarCitasRango(LocalDateTime fechaInicio, LocalDateTime fechaFin) throws BadRequestException;

    /**
     * Registra una nueva cita médica en el sistema.
     *
     * @param citaRq Datos de la cita a crear.
     * @return Respuesta estandarizada con el resultado.
     * @throws BadRequestException Si los datos obligatorios faltan.
     */
    MiRespuestaRS crearCita(CitaRq citaRq) throws BadRequestException;

    /**
     * Actualiza una cita médica existente.
     *
     * @param citaRq Datos actualizados de la cita.
     * @return Respuesta estandarizada con el resultado.
     * @throws BadRequestException Si ocurre un error de validación.
     */
    MiRespuestaRS actualizarCita(CitaRq citaRq) throws BadRequestException;
}