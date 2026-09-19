package com.uniminuto.clinica.api;

import com.uniminuto.clinica.models.AnotacionHistoriaRQ;
import com.uniminuto.clinica.models.AnotacionHistoriaRS;
import com.uniminuto.clinica.exception.BadRequestException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Interfaz que Proporciona métodos para crear, listar y
 * actualizar anotaciones.
*/

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/anotacion-historia")
public interface AnotacionHistoriaApi {


    @PostMapping(value = "/guardar", produces = {"application/json"}, consumes = {"application/json"})
    ResponseEntity<AnotacionHistoriaRS> guardarAnotacion(@RequestBody AnotacionHistoriaRQ request) throws BadRequestException;


    @GetMapping(value = "/listar", produces = {"application/json"})
    ResponseEntity<List<AnotacionHistoriaRS>> listarPorHistoria(
            @RequestParam("historiaId") Integer historiaId
    ) throws BadRequestException;

    /**
     * fecha/hora inicial del rango (formato ISO: yyyy-MM-dd'T'HH:mm:ss).
     */
    @GetMapping(value = "/listar-por-fecha", produces = {"application/json"})
    ResponseEntity<List<AnotacionHistoriaRS>> listarPorRangoFechas(
            @RequestParam("fechaInicial") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicial,
            @RequestParam("fechaFinal") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFinal
    ) throws BadRequestException;


    @PutMapping(value = "/actualizar", produces = {"application/json"}, consumes = {"application/json"})
    ResponseEntity<AnotacionHistoriaRS> actualizarAnotacion(@RequestBody AnotacionHistoriaRQ request) throws BadRequestException;
}