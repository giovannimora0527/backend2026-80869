package com.uniminuto.clinica.service;

import com.uniminuto.clinica.entity.AnotacionHistoria;
import com.uniminuto.clinica.exception.BadRequestException;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Interfaz de servicio para las reglas de negocio de anotaciones de historias médicas.
 */
public interface AnotacionHistoriaService {

    /**
     * Crea y persiste una nueva anotación.
     *
     * @param anotacion Datos de la anotación.
     * @return Anotación guardada.
     * @throws BadRequestException Si los datos ingresados son vacíos o nulos.
     */
    AnotacionHistoria crear(AnotacionHistoria anotacion) throws BadRequestException;

    /**
     * Actualiza una anotación existente.
     *
     * @param id Identificador de la anotación.
     * @param anotacion Datos nuevos a aplicar.
     * @return Anotación actualizada.
     * @throws BadRequestException Si el ID no existe o la observación está vacía.
     */
    AnotacionHistoria actualizar(Long id, AnotacionHistoria anotacion) throws BadRequestException;

    /**
     * Consulta anotaciones filtrando por fechas o retornando todas si no hay filtro.
     *
     * @param fechaInicio Fecha inicial opcional.
     * @param fechaFin Fecha final opcional.
     * @return Lista de anotaciones ordenadas descendentemente.
     * @throws BadRequestException Si el rango de fechas es incoherente.
     */
    List<AnotacionHistoria> listarPorFechas(LocalDateTime fechaInicio, LocalDateTime fechaFin) throws BadRequestException;
}