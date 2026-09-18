package com.uniminuto.clinica.api;

import com.uniminuto.clinica.entity.AnotacionHistoria;
import com.uniminuto.clinica.exception.BadRequestException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Contrato API REST para la gestion de anotaciones de historia medica.
 */
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/anotacion")
public interface AnotacionHistoriaApi {

    /**
     * Endpoint POST para crear una nueva anotacion en el sistema.
     *
     * @param anotacion Objeto JSON con los datos de la nueva anotacion.
     * @return {@link ResponseEntity} con la anotacion creada y estado HTTP 201 Created.
     * @throws BadRequestException Si los datos de entrada son invalidos.
     */
    @PostMapping(value = "/crear", produces = {"application/json"}, consumes = {"application/json"})
    ResponseEntity<AnotacionHistoria> crearAnotacion(@RequestBody AnotacionHistoria anotacion) throws BadRequestException;

    /**
     * Endpoint GET para listar anotaciones entre dos fechas ordenadas descendentemente.
     *
     * @param fechaInicio Fecha inicial en formato ISO (ej: 2026-09-01T00:00:00).
     * @param fechaFin    Fecha final en formato ISO (ej: 2026-09-30T23:59:59).
     * @return {@link ResponseEntity} con la lista de anotaciones filtradas.
     * @throws BadRequestException Si los parametros de fecha son nulos o invalidos.
     */
    @GetMapping(value = "/listar-fechas", produces = {"application/json"})
    ResponseEntity<List<AnotacionHistoria>> listarAnotacionesPorFecha(
            @RequestParam("fechaInicio") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam("fechaFin") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin
    ) throws BadRequestException;

    /**
     * Endpoint POST para actualizar una anotacion existente por su ID.
     *
     * @param id        ID de la anotacion a actualizar (viene en la ruta).
     * @param anotacion Objeto JSON con los nuevos datos de la anotacion.
     * @return {@link ResponseEntity} con la anotacion actualizada y estado HTTP 200 OK.
     * @throws BadRequestException Si la anotacion no existe o los datos son invalidos.
     */
    @PostMapping(value = "/actualizar/{id}", produces = {"application/json"}, consumes = {"application/json"})
    ResponseEntity<AnotacionHistoria> actualizarAnotacion(@PathVariable Long id, @RequestBody AnotacionHistoria anotacion) throws BadRequestException;
}