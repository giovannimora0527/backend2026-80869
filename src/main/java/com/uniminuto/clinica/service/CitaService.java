package com.uniminuto.clinica.service;

import com.uniminuto.clinica.entity.Cita;
import com.uniminuto.clinica.exception.BadRequestException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Interfaz de servicio para la gestion de citas medicas.
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
     * @param fechaFin Fecha final del rango de busqueda.
     * @return Lista de citas dentro del rango.
     * @throws BadRequestException Si las fechas son nulas o incoherentes.
     */
    List<Cita> filtrarCitasPorFecha(LocalDateTime fechaInicio, LocalDateTime fechaFin) throws BadRequestException;

    /**
     * Crea una nueva cita en el sistema.
     *
     * @param cita Objeto con la información de la cita a registrar.
     * @return La cita guardada con su ID generado.
     * @throws BadRequestException Si el objeto enviado es nulo o invalido.
     */
    Cita crearCita(Cita cita) throws BadRequestException;

    /**
     * Actualiza la información de una cita existente dada su clave primaria.
     *
     * @param id Identificador único de la cita a actualizar.
     * @param citaDetalles Objeto con los nuevos datos de la cita.
     * @return La cita actualizada.
     * @throws BadRequestException Si el ID no existe o los datos son nulos.
     */
    Cita actualizarCita(Long id, Cita citaDetalles) throws BadRequestException;
}