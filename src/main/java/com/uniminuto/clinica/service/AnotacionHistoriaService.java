package com.uniminuto.clinica.service;

import com.uniminuto.clinica.models.AnotacionHistoriaRQ;
import com.uniminuto.clinica.models.AnotacionHistoriaRS;
import com.uniminuto.clinica.exception.BadRequestException;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Contrato de los servicios de negocio relacionados con
 * (code AnotacionHistoria)  solo se exponen las operaciones de crear, listar (con filtro de
 * fechas) y actualizar; no se requiere eliminar.
 */
public interface AnotacionHistoriaService {

    /**
     * Crea una nueva anotación dentro de una historia médica.
     */
    AnotacionHistoriaRS crear(AnotacionHistoriaRQ request) throws BadRequestException;

    /**
     * Lista las anotaciones cuya fecha esté entre (fechaInicio) y
     * (fechaFin), ordenadas de la más reciente a la más antigua.
     */
    List<AnotacionHistoriaRS> listarPorRangoFechas(LocalDateTime fechaInicio, LocalDateTime fechaFin) throws BadRequestException;

    /**
     * Lista las anotaciones asociadas a una historia médica puntual.
     */
    List<AnotacionHistoriaRS> listarPorHistoria(Integer historiaId) throws BadRequestException;

    /**
     * Actualiza y nos pide el id de la anotación a actualizar, el id de la historia médica y el texto de la anotación.
     */
    AnotacionHistoriaRS actualizar(AnotacionHistoriaRQ request) throws BadRequestException;
}