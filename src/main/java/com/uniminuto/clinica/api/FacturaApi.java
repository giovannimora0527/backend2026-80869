package com.uniminuto.clinica.api;

import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.models.FacturaRq;
import com.uniminuto.clinica.models.FacturaRs;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.models.PagoRq;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Endpoints del modulo de facturacion.
 */
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/factura")
public interface FacturaApi {

    @GetMapping(value = "/listar", produces = {"application/json"})
    ResponseEntity<List<FacturaRs>> listar() throws BadRequestException;

    @GetMapping(value = "/por-cliente", produces = {"application/json"})
    ResponseEntity<List<FacturaRs>> listarPorCliente(
            @RequestParam("clienteId") Long clienteId) throws BadRequestException;

    @GetMapping(value = "/obtener", produces = {"application/json"})
    ResponseEntity<FacturaRs> obtener(@RequestParam("id") Long id) throws BadRequestException;

    @PostMapping(value = "/generar", produces = {"application/json"}, consumes = {"application/json"})
    ResponseEntity<MiRespuestaRS> generar(@RequestBody FacturaRq facturaRq) throws BadRequestException;

    @PostMapping(value = "/registrar-pago", produces = {"application/json"}, consumes = {"application/json"})
    ResponseEntity<MiRespuestaRS> registrarPago(@RequestBody PagoRq pagoRq) throws BadRequestException;

    @PostMapping(value = "/anular", produces = {"application/json"})
    ResponseEntity<MiRespuestaRS> anular(@RequestParam("id") Long id) throws BadRequestException;

    @GetMapping(value = "/pdf")
    ResponseEntity<byte[]> pdf(@RequestParam("id") Long id) throws BadRequestException;
}
