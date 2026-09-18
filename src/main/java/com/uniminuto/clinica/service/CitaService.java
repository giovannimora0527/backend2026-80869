package com.uniminuto.clinica.service;

import com.uniminuto.clinica.entity.Cita;
import com.uniminuto.clinica.exception.BadRequestException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Interfaz de servicio para la gestion de citas medicas.
 * Define las operaciones de negocio que se pueden realizar con las citas.
 */
public interface CitaService {

    /**
     * Obtiene la lista completa de citas almacenadas.
     *
     * @return Lista de citas.
     * @throws BadRequestException Si ocurre un error de negocio.
     */
    List<Cita> listarCitas() throws BadRequestException;

    /**
     * Filtra las citas en un rango de fechas ordenadas de la mas reciente a la mas antigua.
     *
     * @param fechaInicio Fecha inicial del rango de busqueda.
     * @param fechaFin    Fecha final del rango de busqueda.
     * @return Lista de citas dentro del rango.
     * @throws BadRequestException Si las fechas son nulas o incoherentes.
     */
    List<Cita> filtrarCitasPorFecha(LocalDateTime fechaInicio, LocalDateTime fechaFin) throws BadRequestException;

    /**
     * Crea una nueva cita en el sistema validando sus datos obligatorios.
     *
     * @param cita Objeto Cita con los datos a guardar.
     * @return La cita guardada con su ID generado automáticamente.
     * @throws BadRequestException Si los datos obligatorios son nulos.
     */
    Cita crearCita(Cita cita) throws BadRequestException;

    /**
     * Actualiza los datos de una cita existente identificada por su ID.
     *
     * @param id   ID de la cita a actualizar.
     * @param cita Objeto Cita con los nuevos datos.
     * @return La cita actualizada.
     * @throws BadRequestException Si la cita no existe en el sistema.
     */
    Cita actualizarCita(Long id, Cita cita) throws BadRequestException;
}