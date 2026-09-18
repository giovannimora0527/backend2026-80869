package com.uniminuto.clinica.api;

import com.uniminuto.clinica.entity.Cita;
import com.uniminuto.clinica.exception.BadRequestException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Contrato API REST para la consulta y gestion de citas.
 */
@CrossOrigin(origins = "*")
@RequestMapping("/cita")
public interface CitaApi {

    @GetMapping(value = "/listar", produces = {"application/json"})
    ResponseEntity<List<Cita>> listarCitas() throws BadRequestException;

    @GetMapping(value = "/filtrar-fechas", produces = {"application/json"})
    ResponseEntity<List<Cita>> filtrarCitasPorFecha(
            @RequestParam("fechaInicio") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam("fechaFin") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin
    ) throws BadRequestException;

    @PostMapping(value = "/crear", produces = {"application/json"}, consumes = {"application/json"})
    ResponseEntity<Cita> crearCita(@RequestBody Cita cita) throws BadRequestException;

    @PostMapping(value = "/actualizar/{id}", produces = {"application/json"}, consumes = {"application/json"})
    ResponseEntity<Cita> actualizarCita(@PathVariable Long id, @RequestBody Cita cita) throws BadRequestException;
}