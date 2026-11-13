package com.uniminuto.clinica.service;

import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.models.FacturaRq;
import com.uniminuto.clinica.models.FacturaRs;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.models.PagoRq;

import java.util.List;

/**
 * Servicio de facturacion de servicios veterinarios.
 */
public interface FacturaService {

    /** Lista todas las facturas, de la mas reciente a la mas antigua. */
    List<FacturaRs> listar() throws BadRequestException;

    /** Historial de facturacion de un cliente. */
    List<FacturaRs> listarPorCliente(Long clienteId) throws BadRequestException;

    /** Historial de facturacion de una mascota. */
    List<FacturaRs> listarPorMascota(Long mascotaId) throws BadRequestException;

    /** Obtiene una factura con su detalle y su estado de pago. */
    FacturaRs obtener(Long id) throws BadRequestException;

    /** Genera una factura a partir de una cita y sus items (servicios y medicamentos). */
    MiRespuestaRS generar(FacturaRq facturaRq) throws BadRequestException;

    /** Registra un pago sobre una factura y actualiza su estado. */
    MiRespuestaRS registrarPago(PagoRq pagoRq) throws BadRequestException;

    /** Anula una factura (no elimina registros). */
    MiRespuestaRS anular(Long facturaId) throws BadRequestException;

    /** Genera el PDF de la factura. */
    byte[] generarPdf(Long facturaId) throws BadRequestException;
}
