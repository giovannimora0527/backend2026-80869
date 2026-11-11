package com.uniminuto.clinica.api;

import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.models.FormulaNuevaRq;
import com.uniminuto.clinica.models.FormulaNuevaRs;
import com.uniminuto.clinica.models.MiRespuestaRS;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Endpoints de formulas medicas con varios medicamentos.
 */
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/formula")
public interface FormulaApi {

    @GetMapping(value = "/listar", produces = {"application/json"})
    ResponseEntity<List<FormulaNuevaRs>> listar() throws BadRequestException;

    @GetMapping(value = "/obtener", produces = {"application/json"})
    ResponseEntity<FormulaNuevaRs> obtener(@RequestParam("id") Long id) throws BadRequestException;

    @PostMapping(value = "/guardar", produces = {"application/json"}, consumes = {"application/json"})
    ResponseEntity<MiRespuestaRS> guardar(@RequestBody FormulaNuevaRq formulaRq) throws BadRequestException;

    @GetMapping(value = "/pdf")
    ResponseEntity<byte[]> pdf(@RequestParam("id") Long id) throws BadRequestException;
}
