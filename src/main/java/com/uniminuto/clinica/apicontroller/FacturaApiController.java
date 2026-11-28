package com.uniminuto.clinica.apicontroller;

import com.uniminuto.clinica.serviceimpl.FacturaServiceImpl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/factura")
public class FacturaApiController {

    private final FacturaServiceImpl facturas;

    public FacturaApiController(FacturaServiceImpl facturas) {
        this.facturas = facturas;
    }

    @PostMapping("/cita/{id}/emitir")
    public ResponseEntity<FacturaServiceImpl.FacturaEmitida> emitir(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false)
            String autorizacion,
            @PathVariable("id") long citaId,
            @RequestParam(name = "servicioId", required = false) Long servicioId
    ) {
        return ResponseEntity.status(201)
                .body(facturas.emitir(autorizacion, citaId, servicioId));
    }
    @GetMapping("/cita/{id}")
    public java.util.List<FacturaServiceImpl.FacturaGuardada> consultarPorCita(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false)
            String autorizacion,
            @PathVariable("id") long citaId
    ) {
        return facturas.consultarPorCita(autorizacion, citaId);
    }
    @GetMapping("/citas-atendidas")
    public java.util.List<FacturaServiceImpl.CitaFacturable> buscarCitasAtendidas(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false)
            String autorizacion,
            @RequestParam("busqueda") String busqueda
    ) {
        return facturas.buscarCitasAtendidas(autorizacion, busqueda);
    }

    @GetMapping("/historial")
    public java.util.List<FacturaServiceImpl.FacturaHistorial> buscarHistorial(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false)
            String autorizacion,
            @org.springframework.web.bind.annotation.RequestParam("busqueda")
            String busqueda
    ) {
        return facturas.buscarHistorial(autorizacion, busqueda);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, String>> responderError(ResponseStatusException ex) {
        String mensaje = ex.getReason() == null
                ? "No se pudo emitir la factura." : ex.getReason();

        return ResponseEntity.status(ex.getStatusCode())
                .body(Map.of("message", mensaje));
    }
}