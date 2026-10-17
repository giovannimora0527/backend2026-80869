package com.uniminuto.clinica.api;

import com.uniminuto.clinica.entity.Medicamento;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * Contrato API REST para la gestión y consulta de Medicamentos.
 *
 * @author Yulian Estephania Montealegre
 */
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/medicamento")
public interface MedicamentoApi {

    /**
     * Endpoint para listar todos los medicamentos disponibles.
     *
     * @return Lista de medicamentos en formato JSON.
     */
    @GetMapping(value = "/listar", produces = {"application/json"})
    ResponseEntity<List<Medicamento>> listarMedicamentos();

    /**
     * Endpoint para buscar un medicamento por su ID.
     *
     * @param id ID del medicamento.
     * @return Entidad del medicamento encontrado.
     */
    @GetMapping(value = "/buscar/{id}", produces = {"application/json"})
    ResponseEntity<Medicamento> buscarMedicamentoPorId(@PathVariable Long id);
}