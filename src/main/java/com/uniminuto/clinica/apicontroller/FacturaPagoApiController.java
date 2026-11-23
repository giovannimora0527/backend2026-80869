package com.uniminuto.clinica.apicontroller;

import com.uniminuto.clinica.serviceimpl.FacturaPagoServiceImpl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/factura")
public class FacturaPagoApiController {

    private final FacturaPagoServiceImpl pagoService;

    public FacturaPagoApiController(FacturaPagoServiceImpl pagoService) {
        this.pagoService = pagoService;
    }

    @PostMapping("/{id}/pagos")
    public FacturaPagoServiceImpl.ResultadoPago registrar(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false)
            String autorizacion,
            @PathVariable("id") long facturaId,
            @RequestBody FacturaPagoServiceImpl.PagoRq datos
    ) {
        return pagoService.registrar(autorizacion, facturaId, datos);
    }
    @PutMapping("/{id}/anular")
    public ResponseEntity<Void> anular(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false)
            String autorizacion,
            @PathVariable("id") long facturaId,
            @RequestBody FacturaPagoServiceImpl.AnulacionRq datos
    ) {
        pagoService.anular(autorizacion, facturaId, datos);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, String>> responderError(ResponseStatusException ex) {
        String mensaje = ex.getReason() == null ? "No se pudo registrar el pago." : ex.getReason();
        return ResponseEntity.status(ex.getStatusCode()).body(Map.of("message", mensaje));
    }
}