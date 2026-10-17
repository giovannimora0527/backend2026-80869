package com.uniminuto.clinica.api;

import com.uniminuto.clinica.entity.Cita;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.models.CitaRq;
import com.uniminuto.clinica.models.MiRespuestaRS;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Interfaz que define los endpoints REST para la gestión y agendamiento de Citas Médicas.
 *
 * @author Yulian Estefanía Montealegre
 */
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/cita")
public interface CitaApi {

    /**
     * Lista todas las citas médicas registradas en el sistema.
     *
     * @return Lista de entidades Cita.
     * @throws BadRequestException Si ocurre un error en la petición.
     */
    @GetMapping(value = "/listar", produces = "application/json")
    ResponseEntity<List<Cita>> listarCitas() throws BadRequestException;

    /**
     * Lista las citas médicas comprendidas dentro de un rango de fechas.
     *
     * @param fechaInicio Fecha y hora inicial del filtro.
     * @param fechaFin Fecha y hora final del filtro.
     * @return Lista de citas dentro del rango.
     * @throws BadRequestException Si los parámetros de fecha son incorrectos.
     */
    @GetMapping(value = "/listar-rango", produces = "application/json")
    ResponseEntity<List<Cita>> listarCitasRango(
            @RequestParam LocalDateTime fechaInicio,
            @RequestParam LocalDateTime fechaFin
    ) throws BadRequestException;

    /**
     * Crea una nueva cita médica.
     *
     * @param cita Objeto DTO con los datos de la nueva cita.
     * @return Respuesta estandarizada con el resultado del registro.
     * @throws BadRequestException Si los datos obligatorios no son válidos.
     */
    @PostMapping(value = "/crear", produces = "application/json", consumes = "application/json")
    ResponseEntity<MiRespuestaRS> crearCita(
            @RequestBody CitaRq cita
    ) throws BadRequestException;

    /**
     * Actualiza una cita médica existente.
     *
     * @param cita Objeto DTO con los datos actualizados.
     * @return Respuesta estandarizada con el resultado de la actualización.
     * @throws BadRequestException Si ocurre un error de validación.
     */
    @PostMapping(value = "/actualizar", produces = "application/json", consumes = "application/json")
    ResponseEntity<MiRespuestaRS> actualizarCita(
            @RequestBody CitaRq cita
    ) throws BadRequestException;
}