package com.uniminuto.clinica.apicontroller;

import com.uniminuto.clinica.serviceimpl.PrefacturaServiceImpl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/prefactura")
public class PrefacturaApiController {

    private final PrefacturaServiceImpl prefacturaService;

    public PrefacturaApiController(PrefacturaServiceImpl prefacturaService) {
        this.prefacturaService = prefacturaService;
    }

    @GetMapping(value = "/cita/{id}", produces = "application/json")
    public PrefacturaServiceImpl.Prefactura consultar(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false)
            String autorizacion,
            @PathVariable("id") long citaId,
            @RequestParam(name = "servicioId", required = false) Long servicioId
    ) {
        return prefacturaService.consultar(autorizacion, citaId, servicioId);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, String>> responderError(ResponseStatusException ex) {
        String mensaje = ex.getReason() == null
                ? "No se pudo consultar la prefactura."
                : ex.getReason();

        return ResponseEntity.status(ex.getStatusCode())
                .body(Map.of("message", mensaje));
    }
}