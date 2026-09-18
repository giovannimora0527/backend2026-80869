package com.uniminuto.clinica.apicontroller;

import com.uniminuto.clinica.api.AnotacionHistoriaApi;
import com.uniminuto.clinica.entity.AnotacionHistoria;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.service.AnotacionHistoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Controlador REST que implementa los endpoints definidos en {@link AnotacionHistoriaApi}.
 * Delega la logica de negocio a la capa de servicio.
 */
@RestController
public class AnotacionHistoriaApiController implements AnotacionHistoriaApi {

    /**
     * Servicio de anotaciones inyectado.
     */
    @Autowired
    private AnotacionHistoriaService anotacionService;

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseEntity<AnotacionHistoria> crearAnotacion(@RequestBody AnotacionHistoria anotacion) throws BadRequestException {
        AnotacionHistoria anotacionCreada = this.anotacionService.crearAnotacion(anotacion);
        // Retornamos 201 Created porque se creó un nuevo recurso
        return ResponseEntity.status(HttpStatus.CREATED).body(anotacionCreada);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseEntity<List<AnotacionHistoria>> listarAnotacionesPorFecha(LocalDateTime fechaInicio, LocalDateTime fechaFin) throws BadRequestException {
        return ResponseEntity.ok(this.anotacionService.listarAnotacionesPorFecha(fechaInicio, fechaFin));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseEntity<AnotacionHistoria> actualizarAnotacion(@PathVariable Long id, @RequestBody AnotacionHistoria anotacion) throws BadRequestException {
        AnotacionHistoria anotacionActualizada = this.anotacionService.actualizarAnotacion(id, anotacion);
        // Retornamos 200 OK porque solo modificamos un recurso existente
        return ResponseEntity.ok(anotacionActualizada);
    }
}