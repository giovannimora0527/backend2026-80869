package com.uniminuto.clinica.api;

import com.uniminuto.clinica.models.FormulaMedicaRS;
import com.uniminuto.clinica.exception.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * Contrato de los endpoints REST relacionados con {@code FormulaMedica}
 * (requerimiento 1 del parcial).
 */
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/formula-medica")
public interface FormulaMedicaApi {

    /**
     * Lista las fórmulas médicas del inventario, ordenadas por fecha de
     * creación de la más reciente a la más antigua.
     *
     * @return respuesta HTTP con la lista de fórmulas médicas.
     * @throws BadRequestException si ocurre un error al procesar la solicitud.
     */
    @GetMapping(value = "/listar", produces = {"application/json"})
    ResponseEntity<List<FormulaMedicaRS>> listar() throws BadRequestException;
}
