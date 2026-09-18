package com.uniminuto.clinica.api;

import com.uniminuto.clinica.models.CitaRQ;
import com.uniminuto.clinica.models.CitaRS;
import com.uniminuto.clinica.exception.BadRequestException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.List;


@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/cita")
public interface CitaApi {


    @GetMapping(value = "/listar", produces = {"application/json"})
    ResponseEntity<List<CitaRS>> listarCitas(
            @RequestParam("fechaInicial") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicial,
            @RequestParam("fechaFinal") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFinal
    ) throws BadRequestException;


    @PostMapping(value = "/guardar", produces = {"application/json"}, consumes = {"application/json"})
    ResponseEntity<CitaRS> guardarCita(@RequestBody CitaRQ request) throws BadRequestException;

    /**
     * Se pide el ID
     */
    @PutMapping(value = "/actualizar", produces = {"application/json"}, consumes = {"application/json"})
    ResponseEntity<CitaRS> actualizarCita(@RequestBody CitaRQ request) throws BadRequestException;
}