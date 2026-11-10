package com.uniminuto.clinica.api;

import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.models.CambiarEstadoCitaRq;
import com.uniminuto.clinica.models.CitaRs;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.models.ReprogramarCitaRq;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Endpoints del modulo de calendario de citas.
 */
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/calendario")
public interface CalendarioApi {

    @GetMapping(value = "/citas", produces = {"application/json"})
    ResponseEntity<List<CitaRs>> listarCitas(
            @RequestParam("inicio") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam("fin") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin)
            throws BadRequestException;

    @GetMapping(value = "/historial-mascota", produces = {"application/json"})
    ResponseEntity<List<CitaRs>> historialMascota(
            @RequestParam("mascotaId") Integer mascotaId) throws BadRequestException;

    @PostMapping(value = "/cambiar-estado", produces = {"application/json"}, consumes = {"application/json"})
    ResponseEntity<MiRespuestaRS> cambiarEstado(@RequestBody CambiarEstadoCitaRq rq)
            throws BadRequestException;

    @PostMapping(value = "/reprogramar", produces = {"application/json"}, consumes = {"application/json"})
    ResponseEntity<MiRespuestaRS> reprogramar(@RequestBody ReprogramarCitaRq rq)
            throws BadRequestException;

    @GetMapping(value = "/reporte-excel")
    ResponseEntity<byte[]> reporteExcel(
            @RequestParam(value = "medicoId", required = false) Long medicoId,
            @RequestParam("inicio") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam("fin") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin)
            throws BadRequestException;
}
