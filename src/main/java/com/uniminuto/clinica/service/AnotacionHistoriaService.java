package com.uniminuto.clinica.service;

import com.uniminuto.clinica.entity.AnotacionHistoria;
import com.uniminuto.clinica.exception.BadRequestException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Interfaz de servicio para la gestion de anotaciones de historia medica.
 */
public interface AnotacionHistoriaService {

    AnotacionHistoria crearAnotacion(AnotacionHistoria anotacion) throws BadRequestException;

    List<AnotacionHistoria> listarAnotacionesPorFecha(LocalDateTime fechaInicio, LocalDateTime fechaFin) throws BadRequestException;

    AnotacionHistoria actualizarAnotacion(Long id, AnotacionHistoria anotacion) throws BadRequestException;
}