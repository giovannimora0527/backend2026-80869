package com.uniminuto.clinica.apicontroller;

import com.uniminuto.clinica.api.CitaApi;
import com.uniminuto.clinica.entity.Cita;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.service.CitaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Controlador REST que implementa los endpoints definidos en {@link CitaApi}.
 * Delega la logica de negocio a la capa de servicio.
 */
@RestController
public class CitaApiController implements CitaApi {

    /**
     * Servicio de citas inyectado.
     */
    @Autowired
    private CitaService citaService;

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseEntity<List<Cita>> listarCitas() throws BadRequestException {
        return ResponseEntity.ok(this.citaService.listarCitas());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseEntity<List<Cita>> filtrarCitasPorFecha(LocalDateTime fechaInicio, LocalDateTime fechaFin) throws BadRequestException {
        return ResponseEntity.ok(this.citaService.filtrarCitasPorFecha(fechaInicio, fechaFin));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseEntity<Cita> crearCita(@RequestBody Cita cita) throws BadRequestException {
        Cita citaCreada = this.citaService.crearCita(cita);
        // Retornamos 201 Created porque se creó un nuevo recurso
        return ResponseEntity.status(HttpStatus.CREATED).body(citaCreada);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseEntity<Cita> actualizarCita(@PathVariable Long id, @RequestBody Cita cita) throws BadRequestException {
        Cita citaActualizada = this.citaService.actualizarCita(id, cita);
        // Retornamos 200 OK porque solo modificamos un recurso existente
        return ResponseEntity.ok(citaActualizada);
    }
}