package com.uniminuto.clinica.api;

import com.uniminuto.clinica.entity.Cita;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.models.CitaRq;
import com.uniminuto.clinica.models.MiRespuestaRS;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.List;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/cita")
public interface CitaApi {

    /** Lista las citas registradas. */
    @GetMapping(value = "/listar", produces = "application/json")
    ResponseEntity<List<Cita>> listarCitas() throws BadRequestException;

    /** Consulta las citas entre la fecha inicial y la fecha final. */
    @GetMapping(value = "/listar-rango", produces = "application/json")
    ResponseEntity<List<Cita>> listarCitasRango(
            @RequestParam("fechaInicio") LocalDateTime fechaInicio,
            @RequestParam("fechaFin") LocalDateTime fechaFin
    ) throws BadRequestException;

    /** Lista las citas de una mascota desde la más reciente. */
    @GetMapping(value = "/listar-por-mascota", produces = "application/json")
    ResponseEntity<List<Cita>> listarCitasPorMascota(
            @RequestParam("mascotaId") Integer mascotaId
    ) throws BadRequestException;

    /** Registra una cita médica. */
    @PostMapping(value = "/crear", consumes = "application/json", produces = "application/json")
    ResponseEntity<MiRespuestaRS> crearCita(
            @RequestBody CitaRq cita
    ) throws BadRequestException;

    /** Actualiza una cita médica existente. */
    @PostMapping(value = "/actualizar", consumes = "application/json", produces = "application/json")
    ResponseEntity<MiRespuestaRS> actualizarCita(
            @RequestBody CitaRq cita
    ) throws BadRequestException;
}