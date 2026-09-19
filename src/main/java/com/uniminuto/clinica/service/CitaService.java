package com.uniminuto.clinica.service;

import com.uniminuto.clinica.models.CitaRQ;
import com.uniminuto.clinica.models.CitaRS;
import com.uniminuto.clinica.exception.BadRequestException;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Contrato de los servicios de negocio relacionados con Cita)
 */
public interface CitaService {

    /**
     * Filtra las citas cuya fecha esté entre fechaInicio y
     * fechaFin, ordenadas de la más reciente a la más antigua.
     */
    List<CitaRS> filtrarPorRangoFechas(LocalDateTime fechaInicio, LocalDateTime fechaFin) throws BadRequestException;


    CitaRS crear(CitaRQ request) throws BadRequestException;

    /**
     * Actualiza una cita existente con id
     */
    CitaRS actualizar(CitaRQ request) throws BadRequestException;
}