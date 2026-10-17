package com.uniminuto.clinica.api;

import com.uniminuto.clinica.entity.Medico;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * Contrato API REST para la gestión y consulta de Médicos Veterinarios.
 *
 * @author Yulian Estephania Montealegre
 */
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/medico")
public interface MedicoApi {

    /**
     * Endpoint para listar todos los veterinarios activos.
     *
     * @return Lista de médicos en formato JSON.
     */
    @GetMapping(value = "/listar", produces = {"application/json"})
    ResponseEntity<List<Medico>> listarMedicos();

    /**
     * Endpoint para buscar un médico por su ID.
     *
     * @param id ID del médico.
     * @return Entidad del médico encontrado.
     */
    @GetMapping(value = "/buscar/{id}", produces = {"application/json"})
    ResponseEntity<Medico> buscarMedicoPorId(@PathVariable Long id);
}