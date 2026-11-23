package com.uniminuto.clinica.apicontroller;

import com.uniminuto.clinica.entity.FormulaMedica;
import com.uniminuto.clinica.models.FormulaMedicaRq;
import com.uniminuto.clinica.serviceimpl.AgendaCitasServiceImpl;
import com.uniminuto.clinica.serviceimpl.FormulaServiceImpl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/formula-medica")
public class FormulaDetalleApiController {

    private final FormulaServiceImpl formulaService;
    private final AgendaCitasServiceImpl agendaService;

    public FormulaDetalleApiController(
            FormulaServiceImpl formulaService,
            AgendaCitasServiceImpl agendaService
    ) {
        this.formulaService = formulaService;
        this.agendaService = agendaService;
    }

    @PostMapping(value = "/{id}/medicamentos", consumes = "application/json")
    public ResponseEntity<FormulaMedica> agregarMedicamento(
            @PathVariable("id") Long id,
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false)
            String autorizacion,
            @RequestBody FormulaMedicaRq datos
    ) {
        Long medicoId = agendaService.obtenerMedicoDeSesion(autorizacion);
        return ResponseEntity.ok(formulaService.agregarMedicamento(id, medicoId, datos));
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, String>> responderAcceso(ResponseStatusException ex) {
        String mensaje = ex.getReason() == null ? "Acceso denegado." : ex.getReason();
        return ResponseEntity.status(ex.getStatusCode()).body(Map.of("message", mensaje));
    }
}