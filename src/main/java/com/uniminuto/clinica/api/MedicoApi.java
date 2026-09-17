package com.uniminuto.clinica.api;

import com.uniminuto.clinica.entity.Medico;
import com.uniminuto.clinica.exception.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * Contrato de la API REST para la consulta de información sobre médicos.
 */
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/medico")
public interface MedicoApi {

    /**
     * Endpoint GET para consultar el listado de médicos junto a sus especializaciones.
     *
     * @return {@link ResponseEntity} con la lista de médicos y código HTTP 200 OK.
     * @throws BadRequestException Si se presenta un error en la solicitud.
     */
    @GetMapping(value = "/listar", produces = {"application/json"})
    ResponseEntity<List<Medico>> listarMedicos() throws BadRequestException;
}