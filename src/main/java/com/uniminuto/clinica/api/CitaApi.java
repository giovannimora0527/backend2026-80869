package com.uniminuto.clinica.api;

import com.uniminuto.clinica.entity.Cita;
import com.uniminuto.clinica.exception.BadRequestException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Contrato de la API REST para la gestión integral de citas médicas.
 */
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/cita")
public interface CitaApi {

    /**
     * Obtiene la lista completa de citas médicas registradas.
     *
     * @return {@link ResponseEntity} con la lista de citas y código HTTP 200 OK.
     * @throws BadRequestException Si ocurre un error de negocio al consultar.
     */
    @GetMapping(value = "/listar", produces = {"application/json"})
    ResponseEntity<List<Cita>> listarCitas() throws BadRequestException;

    /**
     * Filtra citas médicas dentro de un rango de fechas.
     *
     * @param fechaInicio Fecha/hora inicial en formato ISO.
     * @param fechaFin Fecha/hora final en formato ISO.
     * @return {@link ResponseEntity} con las citas encontradas.
     * @throws BadRequestException Si los parámetros de fecha son nulos o inválidos.
     */
    @GetMapping(value = "/filtrar-fechas", produces = {"application/json"})
    ResponseEntity<List<Cita>> filtrarCitasPorFecha(
            @RequestParam("fechaInicio") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam("fechaFin") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin
    ) throws BadRequestException;

    /**
     * Registra una nueva cita médica en la base de datos.
     *
     * @param cita Objeto con la información de la cita a crear.
     * @return {@link ResponseEntity} con la cita creada.
     * @throws BadRequestException Si la información enviada es nula o inválida.
     */
    @PostMapping(value = "/crear", consumes = {"application/json"}, produces = {"application/json"})
    ResponseEntity<Cita> crearCita(@RequestBody Cita cita) throws BadRequestException;

    /**
     * Actualiza los datos de una cita médica existente.
     *
     * @param id Identificador único de la cita.
     * @param cita Objeto con los nuevos datos de la cita.
     * @return {@link ResponseEntity} con la cita actualizada.
     * @throws BadRequestException Si el ID no existe o la información es inválida.
     */
    @PutMapping(value = "/actualizar/{id}", consumes = {"application/json"}, produces = {"application/json"})
    ResponseEntity<Cita> actualizarCita(@PathVariable("id") Long id, @RequestBody Cita cita) throws BadRequestException;
}