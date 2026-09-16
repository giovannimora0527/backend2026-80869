package com.uniminuto.clinica.api;

import com.uniminuto.clinica.entity.FormulaMedica;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * Contrato de la API REST para el recurso de Formulas Medicas.
 * Define las rutas, metodos HTTP y politicas CORS.
 */
@RequestMapping("/api/formulas-medicas")
@CrossOrigin(origins = "*")
public interface FormulaMedicaApi {

    /**
     * Endpoint GET para listar todas las formulas medicas en orden descendente por fecha.
     *
     * @return {@link ResponseEntity} con la lista de formulas medicas y estado HTTP 200 OK.
     */
    @GetMapping
    ResponseEntity<List<FormulaMedica>> obtenerTodasOrdenadas();
}