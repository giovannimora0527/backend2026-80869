package com.uniminuto.clinica.service;

import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.models.LogTarifaRs;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.models.ServicioRq;
import com.uniminuto.clinica.models.ServicioRs;

import java.util.List;

/**
 * Servicio de gestion de servicios y tarifas de la clinica.
 */
public interface ServicioService {

    /** Lista todos los servicios. */
    List<ServicioRs> listar() throws BadRequestException;

    /** Lista solo los servicios activos (para usar en atencion y facturacion). */
    List<ServicioRs> listarActivos() throws BadRequestException;

    /** Crea un servicio. */
    MiRespuestaRS crear(ServicioRq servicioRq) throws BadRequestException;

    /** Actualiza un servicio; registra el cambio de tarifa si cambia precio o IVA. */
    MiRespuestaRS actualizar(ServicioRq servicioRq) throws BadRequestException;

    /** Historial de cambios de tarifa de un servicio. */
    List<LogTarifaRs> historialTarifas(Long servicioId) throws BadRequestException;
}
