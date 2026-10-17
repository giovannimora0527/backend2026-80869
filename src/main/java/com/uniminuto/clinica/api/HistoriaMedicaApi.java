package com.uniminuto.clinica.api;

import com.uniminuto.clinica.models.HistoriaMedicaRq;
import com.uniminuto.clinica.models.MiRespuestaRS;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Contrato de la API REST para la gestión de Historias Médicas.
 *
 * @author Yulian Estephania
 * @version 1.0
 */
@Tag(name = "Historia Médica", description = "Endpoints para la gestión de historias clínicas")
@RequestMapping("/historia-medica")
public interface HistoriaMedicaApi {

    /**
     * Lista las historias médicas ordenadas por fecha y hora DESC.
     *
     * @return ResponseEntity con la lista de historias.
     */
    @Operation(summary = "Listar historias médicas", description = "Retorna el listado ordenado por fechaHora DESC")
    @GetMapping("/listar")
    ResponseEntity<MiRespuestaRS> listar();

    /**
     * Busca una historia médica por su ID.
     *
     * @id Identificador de la historia.
     * @return ResponseEntity con la historia encontrada o 404.
     */
    @Operation(summary = "Buscar historia médica", description = "Busca una historia por ID")
    @GetMapping("/buscar/{id}")
    ResponseEntity<MiRespuestaRS> buscarPorId(@PathVariable Long id);

    /**
     * Guarda una nueva historia médica tras validar reglas de negocio.
     *
     * @request Datos de la historia clínica.
     * @return ResponseEntity con la historia creada.
     */
    @Operation(summary = "Guardar historia médica", description = "Crea una historia asociada a una cita ATENDIDA")
    @PostMapping("/guardar")
    ResponseEntity<MiRespuestaRS> guardar(@Valid @RequestBody HistoriaMedicaRq request);
}