package com.uniminuto.clinica.apicontroller;

import com.uniminuto.clinica.serviceimpl.TarifaServiceImpl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/tarifas")
public class TarifaApiController {

    private final TarifaServiceImpl tarifas;

    public TarifaApiController(TarifaServiceImpl tarifas) {
        this.tarifas = tarifas;
    }

    @GetMapping("/servicios")
    public List<TarifaServiceImpl.ServicioTarifado> listarServicios(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false)
            String autorizacion
    ) {
        return tarifas.listarServicios(autorizacion);
    }

    @GetMapping("/servicios/{id}/vigencias")
    public List<TarifaServiceImpl.Vigencia> listarHistorial(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false)
            String autorizacion,
            @PathVariable("id") long id
    ) {
        return tarifas.listarHistorial(autorizacion, id);
    }

    @PostMapping(value = "/servicios/{id}/vigencias", consumes = "application/json")
    public ResponseEntity<Void> registrarVigencia(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false)
            String autorizacion,
            @PathVariable("id") long id,
            @RequestBody TarifaServiceImpl.NuevaVigencia datos
    ) {
        tarifas.registrarVigencia(autorizacion, id, datos);
        return ResponseEntity.status(201).build();
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, String>> responderError(ResponseStatusException ex) {
        String mensaje = ex.getReason() == null
                ? "No se pudo completar la operación de tarifas."
                : ex.getReason();

        return ResponseEntity.status(ex.getStatusCode())
                .body(Map.of("message", mensaje));
    }
}