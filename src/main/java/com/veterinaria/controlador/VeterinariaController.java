package com.veterinaria.controlador;

import com.veterinaria.modelo.*;
import com.veterinaria.servicio.VeterinariaService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api")
public class VeterinariaController {

    private final VeterinariaService servicio;

    public VeterinariaController(VeterinariaService servicio) {
        this.servicio = servicio;
    }

    @GetMapping("/formulas-medicas")
    public ResponseEntity<List<FormulaMedica>> listarFormulas() {
        return ResponseEntity.ok(servicio.listarFormulasOrdenadas());
    }

    @GetMapping("/citas/filtrar")
    public ResponseEntity<List<Cita>> filtrarCitas(
            @RequestParam("inicio") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) java.time.LocalDate inicio,
            @RequestParam("fin") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) java.time.LocalDate fin) {
        return ResponseEntity.ok(servicio.filtrarCitasPorFecha(inicio.atStartOfDay(), fin.atTime(23, 59, 59)));
    }

    @PostMapping("/citas")
    public ResponseEntity<Cita> crearCita(@RequestBody Cita cita) {
        return new ResponseEntity<>(servicio.crearCita(cita), HttpStatus.CREATED);
    }

    @PutMapping("/citas/{id}")
    public ResponseEntity<Cita> actualizarCita(@PathVariable Long id, @RequestBody Cita cita) {
        return ResponseEntity.ok(servicio.actualizarCita(id, cita));
    }

    @PostMapping("/anotaciones-historia")
    public ResponseEntity<AnotacionHistoria> crearAnotacion(
            @RequestParam Long historiaId,
            @RequestBody AnotacionHistoria anotacion) {
        return new ResponseEntity<>(servicio.crearAnotacion(historiaId, anotacion), HttpStatus.CREATED);
    }

    @PutMapping("/anotaciones-historia/{id}")
    public ResponseEntity<AnotacionHistoria> actualizarAnotacion(
            @PathVariable Long id,
            @RequestBody AnotacionHistoria anotacion) {
        return ResponseEntity.ok(servicio.actualizarAnotacion(id, anotacion));
    }

    @GetMapping("/anotaciones-historia")
    public ResponseEntity<List<AnotacionHistoria>> listarAnotaciones(
            @RequestParam("inicio") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) java.time.LocalDate inicio,
            @RequestParam("fin") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) java.time.LocalDate fin) {
        return ResponseEntity.ok(servicio.listarAnotacionesPorFecha(inicio.atStartOfDay(), fin.atTime(23, 59, 59)));
    }

    @GetMapping("/medicos")
    public ResponseEntity<List<Medico>> listarMedicos() {
        return ResponseEntity.ok(servicio.listarMedicosConEspecializaciones());
    }

    @GetMapping("/citas")
    public ResponseEntity<List<Cita>> listarTodasLasCitas() {
        return ResponseEntity.ok(servicio.filtrarCitasPorFecha(
         LocalDateTime.of(2000, 1, 1, 0, 0), 
            LocalDateTime.of(2099, 12, 31, 23, 59)
        ));
    }

    @GetMapping("/historias-medicas")
    public ResponseEntity<List<HistoriaMedica>> listarHistoriasMedicas() {
        return ResponseEntity.ok(servicio.listarHistoriasMedicas());
    }
    
}