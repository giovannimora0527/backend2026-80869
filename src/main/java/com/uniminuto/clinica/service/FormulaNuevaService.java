package com.uniminuto.clinica.service;

import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.models.FormulaNuevaRq;
import com.uniminuto.clinica.models.FormulaNuevaRs;
import com.uniminuto.clinica.models.MiRespuestaRS;

import java.util.List;

/**
 * Servicio de formulas medicas con varios medicamentos.
 *
 * Importante: crear una formula NO descuenta el inventario. El descuento ocurre
 * al confirmar la dispensacion en el modulo de despacho (Fase 5).
 */
public interface FormulaNuevaService {

    /** Lista las formulas (cabecera con su detalle). */
    List<FormulaNuevaRs> listar() throws BadRequestException;

    /** Obtiene una formula con su detalle. */
    FormulaNuevaRs obtener(Long id) throws BadRequestException;

    /** Crea una formula con uno o varios medicamentos. */
    MiRespuestaRS crear(FormulaNuevaRq rq) throws BadRequestException;

    /** Genera el PDF de una formula. */
    byte[] generarPdf(Long id) throws BadRequestException;
}
