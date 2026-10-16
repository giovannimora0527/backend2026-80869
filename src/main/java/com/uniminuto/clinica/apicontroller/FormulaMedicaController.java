package com.uniminuto.clinica.apicontroller;

import com.uniminuto.clinica.entity.FormulaMedica;
import com.uniminuto.clinica.service.FormulaMedicaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para gestionar fórmulas médicas.
 * @author Yulian Montealegre
 */
@RestController
@RequestMapping("/api/formulas-medicas")
// Aquí agregamos los permisos completos de CORS que faltaban
@CrossOrigin(origins = "http://localhost:4200", allowedHeaders = "*", allowCredentials = "true")
public class FormulaMedicaController {

    @Autowired
    private FormulaMedicaService formulaMedicaService;

    /**
     * Obtiene todas las fórmulas médicas.
     */
    @GetMapping
    public ResponseEntity<List<FormulaMedica>> obtenerFormulas() {
        List<FormulaMedica> formulas = formulaMedicaService.obtenerTodas();
        return ResponseEntity.ok(formulas);
    }

    /**
     * Crea una nueva fórmula médica.
     */
    @PostMapping("/crear")
    public ResponseEntity<FormulaMedica> crearFormula(@RequestBody FormulaMedica formula) {
        FormulaMedica nueva = formulaMedicaService.crear(formula);
        return ResponseEntity.ok(nueva);
    }

    /**
     * Actualiza una fórmula médica existente.
     */
    @PutMapping("/actualizar/{id}")
    public ResponseEntity<FormulaMedica> actualizarFormula(
            @PathVariable Long id,
            @RequestBody FormulaMedica formula) {
        FormulaMedica actualizada = formulaMedicaService.actualizar(id, formula);
        return ResponseEntity.ok(actualizada);
    }
}