package com.uniminuto.clinica.service;

import com.uniminuto.clinica.entity.Cita;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.models.CitaRequest;
import com.uniminuto.clinica.models.MiRespuestaRS;

import java.time.LocalDateTime;
import java.util.List;

public interface CitaService {
    List<Cita> listarCitas() throws BadRequestException;

    /**
     * Filtra las citas registradas entre dos fechas.
     *
     * @param fechaInicial limite inferior del rango de fechas.
     * @param fechaFinal limite superior del rango de fechas.
     * @return lista de citas ordenadas de la mas reciente a la mas antigua.
     * @throws BadRequestException excepcion.
     */
    List<Cita> filtrarCitas(LocalDateTime fechaInicial, LocalDateTime fechaFinal)
            throws BadRequestException;

    Cita crearCita(CitaRequest citaRequest) throws BadRequestException;

    Cita actualizarCita(Integer id, CitaRequest citaRequest) throws BadRequestException;

    MiRespuestaRS eliminarCita(Integer id) throws BadRequestException;
}
