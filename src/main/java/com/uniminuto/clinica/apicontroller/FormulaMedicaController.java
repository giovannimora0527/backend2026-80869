package com.uniminuto.clinica.apicontroller;

import com.uniminuto.clinica.api.FormulaMedicaApi;
import com.uniminuto.clinica.entity.FormulaMedica;
import com.uniminuto.clinica.models.FormulaMedicaRq;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.service.FormulaMedicaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para manejar peticiones HTTP de Fórmulas Médicas.
 *
 * @author Yulian Estefanía Montealegre
 */
@RestController
@RequestMapping("/formulas")
public class FormulaMedicaController implements FormulaMedicaApi {

    @Autowired
    private FormulaMedicaService formulaMedicaService;

    @GetMapping
    @Override
    public ResponseEntity<List<FormulaMedica>> listarFormulas() {
        return ResponseEntity.ok(formulaMedicaService.obtenerTodas());
    }

    @PostMapping
    @Override
    public ResponseEntity<MiRespuestaRS> guardarFormula(@RequestBody FormulaMedicaRq request) {
        return ResponseEntity.ok(formulaMedicaService.crearFormula(request));
    }

    @PutMapping("/{id}")
    @Override
    public ResponseEntity<MiRespuestaRS> actualizarFormula(@PathVariable Long id, @RequestBody FormulaMedicaRq request) {
        return ResponseEntity.ok(formulaMedicaService.actualizarFormula(id, request));
    }
}