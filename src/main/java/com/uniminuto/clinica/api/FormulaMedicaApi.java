package com.uniminuto.clinica.api;

import com.uniminuto.clinica.entity.FormulaMedica;
import com.uniminuto.clinica.exception.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.List;

/**
 * Contrato de la API REST para el recurso de Formulas Medicas.
 */
@RequestMapping("/api/formulas-medicas")
@CrossOrigin(origins = "*")
public interface FormulaMedicaApi {

    /**
     * Endpoint GET para listar todas las formulas medicas en orden descendente por fecha.
     * @return ResponseEntity con la lista de formulas medicas y estado HTTP 200 OK.
     * @throws BadRequestException Si ocurre un error.
     */
    @GetMapping
    ResponseEntity<List<FormulaMedica>> obtenerTodasOrdenadas() throws BadRequestException;
}