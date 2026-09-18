package com.uniminuto.clinica.service;

import com.uniminuto.clinica.entity.AnotacionHistoria;
import com.uniminuto.clinica.exception.BadRequestException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Interfaz de servicio para la gestion de anotaciones de historia medica.
 */
public interface AnotacionHistoriaService {

    /**
     * Crea una nueva anotacion en el sistema validando sus datos obligatorios.
     *
     * @param anotacion Objeto AnotacionHistoria con los datos a guardar.
     * @return La anotacion guardada con su ID generado automaticamente.
     * @throws BadRequestException Si los datos obligatorios son nulos.
     */
    AnotacionHistoria crearAnotacion(AnotacionHistoria anotacion) throws BadRequestException;

    /**
     * Lista todas las anotaciones dentro de un rango de fechas ordenadas descendentemente.
     *
     * @param fechaInicio Fecha inicial del rango de busqueda.
     * @param fechaFin    Fecha final del rango de busqueda.
     * @return Lista de anotaciones dentro del rango.
     * @throws BadRequestException Si las fechas son nulas o incoherentes.
     */
    List<AnotacionHistoria> listarAnotacionesPorFecha(LocalDateTime fechaInicio, LocalDateTime fechaFin) throws BadRequestException;

    /**
     * Actualiza los datos de una anotacion existente identificada por su ID.
     *
     * @param id        ID de la anotacion a actualizar.
     * @param anotacion Objeto AnotacionHistoria con los nuevos datos.
     * @return La anotacion actualizada.
     * @throws BadRequestException Si la anotacion no existe en el sistema.
     */
    AnotacionHistoria actualizarAnotacion(Long id, AnotacionHistoria anotacion) throws BadRequestException;
}