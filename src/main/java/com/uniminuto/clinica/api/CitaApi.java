package main.java.com.uniminuto.clinica.api;

import com.uniminuto.clinica.models.Cita;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Interfaz de la API para la gestión de citas médicas.
 */
@RequestMapping("/api/citas")
public interface CitaApi {

    @PostMapping
    ResponseEntity<?> crearCita(@RequestBody Cita cita);

    @PutMapping("/{id}")
    ResponseEntity<?> actualizarCita(@PathVariable("id") Long id, @RequestBody Cita cita);

    @GetMapping("/filtrar")
    ResponseEntity<List<Cita>> filtrarCitas(
            @RequestParam("inicio") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam("fin") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin);
}