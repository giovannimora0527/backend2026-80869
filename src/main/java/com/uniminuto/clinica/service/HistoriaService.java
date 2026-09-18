package com.uniminuto.clinica.service;

import com.uniminuto.clinica.entity.AnotacionHistoria;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.models.AnotacionHistoriaRequest;
import com.uniminuto.clinica.models.HistoriaConAnotacionesRS;
import com.uniminuto.clinica.models.MiRespuestaRS;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Interfaz que define los servicios para manejar las historias médicas y sus anotaciones.
 */
public interface HistoriaService {
    /**
     * Crea una anotacion.
     * @param request payload
     * @return anotacion
     * @throws BadRequestException ex
     */
    AnotacionHistoria crearAnotacion(AnotacionHistoriaRequest request) throws BadRequestException;
    /**
     * Actualiza una anotacion.
     * @param id id
     * @param request payload
     * @return anotacion
     * @throws BadRequestException ex
     */
    AnotacionHistoria actualizarAnotacion(Integer id, AnotacionHistoriaRequest request) throws BadRequestException;
    /**
     * Lista historias.
     * @param historiaId id (opcional)
     * @param fechaInicial inicio (opcional)
     * @param fechaFinal fin (opcional)
     * @return lista
     * @throws BadRequestException ex
     */
    List<HistoriaConAnotacionesRS> listarHistorias(Integer historiaId, LocalDateTime fechaInicial, LocalDateTime fechaFinal) throws BadRequestException;
    /**
     * Elimina una anotacion.
     * @param id id
     * @return respuesta
     * @throws BadRequestException ex
     */
    MiRespuestaRS eliminarAnotacion(Integer id) throws BadRequestException;
    /**
     * Elimina una historia.
     * @param id id
     * @return respuesta
     * @throws BadRequestException ex
     */
    MiRespuestaRS eliminarHistoria(Integer id) throws BadRequestException;
}
