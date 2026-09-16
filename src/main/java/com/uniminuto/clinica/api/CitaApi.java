package com.uniminuto.clinica.api;

import com.uniminuto.clinica.entity.Cita;
import com.uniminuto.clinica.exception.BadRequestException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Contrato API REST para la consulta y gestion de citas.
 */
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/cita")
public interface CitaApi {

    /**
     * Metodo para listar todas las citas del sistema.
     *
     * @return {@link ResponseEntity} con la lista de citas.
     * @throws BadRequestException Si ocurre un error de ejecucion.
     */
    @GetMapping(value = "/listar", produces = {"application/json"})
    ResponseEntity<List<Cita>> listarCitas() throws BadRequestException;

    /**
     * Endpoint GET para filtrar citas entre dos fechas ordenadas descendentemente.
     *
     * @param fechaInicio Fecha inicial en formato ISO (ej: 2026-09-01T00:00:00).
     * @param fechaFin Fecha final en formato ISO (ej: 2026-09-30T23:59:59).
     * @return {@link ResponseEntity} con la lista de citas filtradas.
     * @throws BadRequestException Si los parametros de fecha son nulos o invalidos.
     */
    @GetMapping(value = "/filtrar-fechas", produces = {"application/json"})
    ResponseEntity<List<Cita>> filtrarCitasPorFecha(
            @RequestParam("fechaInicio") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam("fechaFin") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin
    ) throws BadRequestException;
}