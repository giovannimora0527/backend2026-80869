package com.uniminuto.clinica.api;

import com.uniminuto.clinica.entity.AnotacionHistoria;
import com.uniminuto.clinica.exception.BadRequestException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Contrato API REST para la gestión de anotaciones en historias médicas.
 */
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/anotacion-historia")
public interface AnotacionHistoriaApi {

    /**
     * Registra una nueva anotación.
     *
     * @param anotacion Objeto anotación enviado en el JSON.
     * @return {@link ResponseEntity} con el objeto creado.
     * @throws BadRequestException Si la observación está vacía.
     */
    @PostMapping(value = "/crear", consumes = {"application/json"}, produces = {"application/json"})
    ResponseEntity<AnotacionHistoria> crear(@RequestBody AnotacionHistoria anotacion) throws BadRequestException;

    /**
     * Actualiza una anotación existente.
     *
     * @param id Identificador de la anotación.
     * @param anotacion Nuevos datos.
     * @return {@link ResponseEntity} con el objeto modificado.
     * @throws BadRequestException Si el ID no existe o la observación es nula.
     */
    @PutMapping(value = "/actualizar/{id}", consumes = {"application/json"}, produces = {"application/json"})
    ResponseEntity<AnotacionHistoria> actualizar(@PathVariable("id") Long id, @RequestBody AnotacionHistoria anotacion) throws BadRequestException;

    /**
     * Lista anotaciones ordenadas por fecha descendente con o sin rango de fechas.
     *
     * @param fechaInicio Fecha inicial opcional en formato ISO.
     * @param fechaFin Fecha final opcional en formato ISO.
     * @return {@link ResponseEntity} con el listado.
     * @throws BadRequestException Si el rango de fechas es inválido.
     */
    @GetMapping(value = "/filtrar-fechas", produces = {"application/json"})
    ResponseEntity<List<AnotacionHistoria>> listar(
            @RequestParam(value = "fechaInicio", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam(value = "fechaFin", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin
    ) throws BadRequestException;
}