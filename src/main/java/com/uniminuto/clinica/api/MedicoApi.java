package com.uniminuto.clinica.api;

import com.uniminuto.clinica.entity.Medico;
import com.uniminuto.clinica.exception.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.List;

/**
 * Contrato API REST para la gestion de medicos.
 */
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/medico")
public interface MedicoApi {

    /**
     * Endpoint GET para listar todos los medicos con sus especializaciones.
     *
     * @return {@link ResponseEntity} con la lista de medicos y estado HTTP 200 OK.
     * @throws BadRequestException Si ocurre un error de ejecucion.
     */
    @GetMapping(value = "/listar-con-especializaciones", produces = {"application/json"})
    ResponseEntity<List<Medico>> listarMedicosConEspecializaciones() throws BadRequestException;
}