package com.uniminuto.clinica.service;

import com.uniminuto.clinica.models.HistoriaMedicaRQ;
import com.uniminuto.clinica.models.HistoriaMedicaRS;
import com.uniminuto.clinica.exception.BadRequestException;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Contrato del CRUD de negocio para (HistoriaMedica)
 */
public interface HistoriaMedicaService {

    /**
     * Lista las historias médicas cuya (fechaCreacion) esté entre
     * (fechaInicial) y (fechaFinal), ordenadas de la más
     * reciente a la más antigua.
     */
    List<HistoriaMedicaRS> listar(LocalDateTime fechaInicial, LocalDateTime fechaFinal) throws BadRequestException;

    /**
     * Obtiene una historia médica por su identificador.
     */
    HistoriaMedicaRS obtenerPorId(Long id) throws BadRequestException;

    /**
     * Crea una nueva historia médica.
     */
    HistoriaMedicaRS crear(HistoriaMedicaRQ request) throws BadRequestException;

    /**
     * Actualiza una historia médica existente. El identificador a modificar
     */
    HistoriaMedicaRS actualizar(HistoriaMedicaRQ request) throws BadRequestException;

    /**
     * Elimina una historia médica del sistema por medio del identificador.
     */
    void eliminar(Long id) throws BadRequestException;
}
