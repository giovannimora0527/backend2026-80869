package com.uniminuto.clinica.api;

import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.models.LogTarifaRs;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.models.ServicioRq;
import com.uniminuto.clinica.models.ServicioRs;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Endpoints de gestion de servicios y tarifas.
 */
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/servicio")
public interface ServicioApi {

    @GetMapping(value = "/listar", produces = {"application/json"})
    ResponseEntity<List<ServicioRs>> listar() throws BadRequestException;

    @GetMapping(value = "/listar-activos", produces = {"application/json"})
    ResponseEntity<List<ServicioRs>> listarActivos() throws BadRequestException;

    @PostMapping(value = "/guardar", produces = {"application/json"}, consumes = {"application/json"})
    ResponseEntity<MiRespuestaRS> guardar(@RequestBody ServicioRq servicioRq) throws BadRequestException;

    @PostMapping(value = "/actualizar", produces = {"application/json"}, consumes = {"application/json"})
    ResponseEntity<MiRespuestaRS> actualizar(@RequestBody ServicioRq servicioRq) throws BadRequestException;

    @GetMapping(value = "/historial-tarifas", produces = {"application/json"})
    ResponseEntity<List<LogTarifaRs>> historialTarifas(
            @RequestParam("servicioId") Long servicioId) throws BadRequestException;
}
