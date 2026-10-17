package com.uniminuto.clinica.apicontroller;

import com.uniminuto.clinica.api.CitaApi;
import com.uniminuto.clinica.entity.Cita;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.models.CitaRq;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.service.CitaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Controlador REST que implementa los endpoints de CitaApi
 * para la gestión y agendamiento de citas médicas veterinarias.
 *
 * @author Yulian Estefanía Montealegre
 */
@RestController
@RequestMapping("/citas")
public class CitaApiController implements CitaApi {

    @Autowired
    private CitaService citaService;

    @GetMapping
    @Override
    public ResponseEntity<List<Cita>> listarCitas() throws BadRequestException {
        return ResponseEntity.ok(this.citaService.listarCitas());
    }

    @GetMapping("/rango")
    @Override
    public ResponseEntity<List<Cita>> listarCitasRango(
            @RequestParam LocalDateTime fechaInicio,
            @RequestParam LocalDateTime fechaFin) throws BadRequestException {
        return ResponseEntity.ok(this.citaService.listarCitasRango(fechaInicio, fechaFin));
    }

    @PostMapping
    @Override
    public ResponseEntity<MiRespuestaRS> crearCita(@RequestBody CitaRq cita) throws BadRequestException {
        return ResponseEntity.ok(this.citaService.crearCita(cita));
    }

    @PutMapping
    @Override
    public ResponseEntity<MiRespuestaRS> actualizarCita(@RequestBody CitaRq cita) throws BadRequestException {
        return ResponseEntity.ok(this.citaService.actualizarCita(cita));
    }
}