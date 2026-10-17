package com.uniminuto.clinica.api;

import com.uniminuto.clinica.entity.AnotacionHistoria;
import com.uniminuto.clinica.models.AnotacionHistoriaRq;
import com.uniminuto.clinica.models.MiRespuestaRS;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrato de la API REST para la gestión de anotaciones en las historias clínicas.
 * Define los endpoints para registrar nuevas anotaciones y consultarlas por historia.
 *
 * @author Yulian Estephania Montealegre
 * @version 1.0
 */
@Tag(name = "Anotación Historia", description = "Endpoints para la gestión de anotaciones clínicas")
@RequestMapping("/anotacion-historia")
public interface AnotacionHistoriaApi {

    @Operation(summary = "Crear anotación", description = "Crea una nueva anotación vinculada a una historia clínica existente")
    @PostMapping("/crear")
    ResponseEntity<MiRespuestaRS> crearAnotacionHistoria(@Valid @RequestBody AnotacionHistoriaRq anotacionHistoriaRq);

    @Operation(summary = "Listar anotaciones por historia", description = "Retorna las anotaciones de una historia clínica ordenadas por fecha DESC")
    @GetMapping("/listar-por-historia/{historiaId}")
    ResponseEntity<List<AnotacionHistoria>> listarPorHistoria(@PathVariable Long historiaId);
}