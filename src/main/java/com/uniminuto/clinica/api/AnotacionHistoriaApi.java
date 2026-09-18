package com.uniminuto.clinica.api;

import com.uniminuto.clinica.entity.AnotacionHistoria;
import com.uniminuto.clinica.exception.BadRequestException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Contrato API REST para la gestion de anotaciones de historia medica.
 */
@CrossOrigin(origins = "*")
@RequestMapping("/anotacion")
public interface AnotacionHistoriaApi {

    @PostMapping(value = "/crear", produces = {"application/json"}, consumes = {"application/json"})
    ResponseEntity<AnotacionHistoria> crearAnotacion(@RequestBody AnotacionHistoria anotacion) throws BadRequestException;

    @GetMapping(value = "/listar-fechas", produces = {"application/json"})
    ResponseEntity<List<AnotacionHistoria>> listarAnotacionesPorFecha(
            @RequestParam("fechaInicio") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam("fechaFin") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin
    ) throws BadRequestException;

    @PostMapping(value = "/actualizar/{id}", produces = {"application/json"}, consumes = {"application/json"})
    ResponseEntity<AnotacionHistoria> actualizarAnotacion(@PathVariable Long id, @RequestBody AnotacionHistoria anotacion) throws BadRequestException;
}