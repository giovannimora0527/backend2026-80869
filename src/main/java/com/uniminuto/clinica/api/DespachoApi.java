package com.uniminuto.clinica.api;

import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.models.DespachoRq;
import com.uniminuto.clinica.models.DespachoRs;
import com.uniminuto.clinica.models.FormulaPendienteRs;
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
 * Endpoints del modulo de despacho. Restringido a DESPACHO_MEDICO y
 * Administrador mediante el filtro de seguridad.
 */
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/despacho")
public interface DespachoApi {

    @GetMapping(value = "/formulas-pendientes", produces = {"application/json"})
    ResponseEntity<List<FormulaPendienteRs>> formulasPendientes() throws BadRequestException;

    @GetMapping(value = "/formula", produces = {"application/json"})
    ResponseEntity<FormulaPendienteRs> obtenerFormula(
            @RequestParam("formulaId") Long formulaId) throws BadRequestException;

    @PostMapping(value = "/confirmar", produces = {"application/json"}, consumes = {"application/json"})
    ResponseEntity<MiRespuestaRS> confirmar(@RequestBody DespachoRq despachoRq) throws BadRequestException;

    @GetMapping(value = "/listar", produces = {"application/json"})
    ResponseEntity<List<DespachoRs>> listar() throws BadRequestException;

    @GetMapping(value = "/comprobante")
    ResponseEntity<byte[]> comprobante(@RequestParam("id") Long id) throws BadRequestException;
}
