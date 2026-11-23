package com.uniminuto.clinica.apicontroller;

import com.uniminuto.clinica.serviceimpl.DespachoServiceImpl;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/despacho")
public class DespachoApiController {

    private final DespachoServiceImpl despachoService;

    public DespachoApiController(DespachoServiceImpl despachoService) {
        this.despachoService = despachoService;
    }

    @GetMapping(value = "/formulas/buscar", produces = "application/json")
    public ResponseEntity<List<DespachoServiceImpl.FormulaEncontrada>> buscarFormulas(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false)
            String autorizacion,
            @RequestParam(name = "texto", required = false) String texto,
            @RequestParam(name = "fecha", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha
    ) {
        return ResponseEntity.ok(
                despachoService.buscarFormulas(autorizacion, texto, fecha)
        );
    }

    @GetMapping(value = "/formula/{id}", produces = "application/json")
    public ResponseEntity<List<DespachoServiceImpl.LineaPendiente>> consultarFormula(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false)
            String autorizacion,
            @PathVariable("id") Long id
    ) {
        return ResponseEntity.ok(despachoService.consultarFormula(autorizacion, id));
    }

    @PutMapping("/formula/{id}/preparacion")
    public ResponseEntity<Void> iniciarPreparacion(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false)
            String autorizacion,
            @PathVariable("id") Long id
    ) {
        despachoService.iniciarPreparacion(autorizacion, id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/formula/{id}/pendiente-disponibilidad")
    public ResponseEntity<Void> marcarPendientePorDisponibilidad(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false)
            String autorizacion,
            @PathVariable("id") Long id
    ) {
        despachoService.marcarPendientePorDisponibilidad(autorizacion, id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping(value = "/formula/{id}/cancelacion", consumes = "application/json")
    public ResponseEntity<Void> cancelarDespacho(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false)
            String autorizacion,
            @PathVariable("id") Long id,
            @RequestBody DespachoServiceImpl.CancelacionRq datos
    ) {
        despachoService.cancelarDespacho(autorizacion, id, datos);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/confirmar", consumes = "application/json")
    public ResponseEntity<DespachoServiceImpl.ComprobanteEntrega> confirmar(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false)
            String autorizacion,
            @RequestBody DespachoServiceImpl.EntregaRq datos
    ) {
        return ResponseEntity.ok(despachoService.confirmar(autorizacion, datos));
    }

    @GetMapping(value = "/formula/{id}/comprobantes", produces = "application/json")
    public ResponseEntity<List<DespachoServiceImpl.FilaComprobante>> listarComprobantes(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false)
            String autorizacion,
            @PathVariable("id") Long id
    ) {
        return ResponseEntity.ok(despachoService.listarComprobantes(autorizacion, id));
    }

    @GetMapping(value = "/formula/{id}/historial", produces = "application/json")
    public ResponseEntity<List<DespachoServiceImpl.CambioEstado>> listarHistorial(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false)
            String autorizacion,
            @PathVariable("id") Long id
    ) {
        return ResponseEntity.ok(despachoService.listarHistorial(autorizacion, id));
    }

    @GetMapping(value = "/salidas", produces = "application/json")
    public ResponseEntity<List<DespachoServiceImpl.SalidaMedicamento>> buscarSalidasPorMedicamento(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false)
            String autorizacion,
            @RequestParam("medicamento") String medicamento
    ) {
        return ResponseEntity.ok(
                despachoService.buscarSalidasPorMedicamento(autorizacion, medicamento)
        );
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, String>> responderError(ResponseStatusException ex) {
        String mensaje = ex.getReason() == null
                ? "No se pudo completar la operación de despacho."
                : ex.getReason();

        return ResponseEntity.status(ex.getStatusCode())
                .body(Map.of("message", mensaje));
    }
}