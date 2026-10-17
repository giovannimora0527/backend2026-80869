package com.uniminuto.clinica.api;

import com.uniminuto.clinica.entity.FormulaMedica;
import com.uniminuto.clinica.models.FormulaMedicaRq;
import com.uniminuto.clinica.models.MiRespuestaRS;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrato API REST para la gestión de Fórmulas Médicas.
 *
 * @author Yulian Estephania Montealegre
 */
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/formula-medica")
public interface FormulaMedicaApi {

    /**
     * Endpoint para listar todas las fórmulas médicas.
     *
     * @return Lista de fórmulas.
     */
    @GetMapping(value = "/listar", produces = {"application/json"})
    ResponseEntity<List<FormulaMedica>> listarFormulas();

    /**
     * Endpoint para registrar una fórmula médica.
     *
     * @param request Datos de la fórmula.
     * @return Respuesta estandarizada.
     */
    @PostMapping(value = "/guardar", produces = {"application/json"}, consumes = {"application/json"})
    ResponseEntity<MiRespuestaRS> guardarFormula(@RequestBody FormulaMedicaRq request);

    /**
     * Endpoint para actualizar una fórmula médica.
     *
     * @param id ID de la fórmula.
     * @param request Datos a actualizar.
     * @return Respuesta estandarizada.
     */
    @PutMapping(value = "/actualizar/{id}", produces = {"application/json"}, consumes = {"application/json"})
    ResponseEntity<MiRespuestaRS> actualizarFormula(@PathVariable Long id, @RequestBody FormulaMedicaRq request);
}