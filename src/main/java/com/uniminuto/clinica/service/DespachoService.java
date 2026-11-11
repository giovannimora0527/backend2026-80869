package com.uniminuto.clinica.service;

import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.models.DespachoRq;
import com.uniminuto.clinica.models.DespachoRs;
import com.uniminuto.clinica.models.FormulaPendienteRs;
import com.uniminuto.clinica.models.MiRespuestaRS;

import java.util.List;

/**
 * Servicio del modulo de despacho y dispensacion de medicamentos.
 *
 * La dispensacion descuenta del inventario priorizando los lotes de vencimiento
 * mas proximo (FEFO), no permite dispensar vencidos ni entregar mas que las
 * existencias, admite entregas parciales y registra los movimientos de salida.
 * El descuento se realiza de forma transaccional al confirmar el despacho.
 */
public interface DespachoService {

    /** Lista las formulas pendientes de despacho con su disponibilidad. */
    List<FormulaPendienteRs> listarFormulasPendientes() throws BadRequestException;

    /** Detalle de una formula para preparar su despacho. */
    FormulaPendienteRs obtenerFormulaParaDespacho(Long formulaId) throws BadRequestException;

    /** Confirma el despacho: descuenta inventario y registra los movimientos. */
    MiRespuestaRS confirmarDespacho(DespachoRq rq) throws BadRequestException;

    /** Historial de despachos realizados. */
    List<DespachoRs> listarDespachos() throws BadRequestException;

    /** Comprobante de despacho en PDF. */
    byte[] generarComprobantePdf(Long despachoId) throws BadRequestException;
}
