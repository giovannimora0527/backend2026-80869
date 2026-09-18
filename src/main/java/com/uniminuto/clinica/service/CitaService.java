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
     * ¿Por qué está aquí y no en el Repository?
     * - Porque aquí definimos la "regla de negocio".
     * - Más adelante, en el ServiceImpl, agregaremos validaciones
     *   (como verificar que las fechas no sean nulas) antes de llamar al Repository.
     *
     * @param fechaInicio Fecha inicial del rango de busqueda.
     * @param fechaFin    Fecha final del rango de busqueda.
     * @return Lista de citas dentro del rango.
     * @throws BadRequestException Si las fechas son nulas o incoherentes.
     */
    List<Cita> filtrarCitasPorFecha(LocalDateTime fechaInicio, LocalDateTime fechaFin) throws BadRequestException;
}