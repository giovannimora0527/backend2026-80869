package com.uniminuto.clinica.apicontroller;

import com.uniminuto.clinica.api.HistoriaMedicaApi;
import com.uniminuto.clinica.models.HistoriaMedicaRq;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.service.HistoriaMedicaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST que implementa los endpoints de Historia Médica.
 *
 * @author Yulian Estefania
 * @version 1.0
 */
@RestController
@RequestMapping("/historias")
@RequiredArgsConstructor
public class HistoriaMedicaApiController implements HistoriaMedicaApi {

    private final HistoriaMedicaService historiaMedicaService;

    @GetMapping
    @Override
    public ResponseEntity<MiRespuestaRS> listar() {
        return ResponseEntity.ok(historiaMedicaService.listar());
    }

    @GetMapping("/{id}")
    @Override
    public ResponseEntity<MiRespuestaRS> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(historiaMedicaService.buscarPorId(id));
    }

    @PostMapping
    @Override
    public ResponseEntity<MiRespuestaRS> guardar(@RequestBody HistoriaMedicaRq request) {
        return ResponseEntity.ok(historiaMedicaService.guardar(request));
    }
}