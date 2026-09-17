package com.uniminuto.clinica.apicontroller;

import com.uniminuto.clinica.api.AnotacionHistoriaApi;
import com.uniminuto.clinica.entity.AnotacionHistoria;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.service.AnotacionHistoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Controlador REST que implementa los endpoints definidos en {@link AnotacionHistoriaApi}.
 */
@RestController
public class AnotacionHistoriaApiController implements AnotacionHistoriaApi {

    @Autowired
    private AnotacionHistoriaService service;

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseEntity<AnotacionHistoria> crear(@RequestBody AnotacionHistoria anotacion) throws BadRequestException {
        return ResponseEntity.ok(service.crear(anotacion));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseEntity<AnotacionHistoria> actualizar(@PathVariable("id") Long id, @RequestBody AnotacionHistoria anotacion) throws BadRequestException {
        return ResponseEntity.ok(service.actualizar(id, anotacion));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseEntity<List<AnotacionHistoria>> listar(@RequestParam(value = "fechaInicio", required = false) LocalDateTime fechaInicio, @RequestParam(value = "fechaFin", required = false) LocalDateTime fechaFin) throws BadRequestException {
        return ResponseEntity.ok(service.listarPorFechas(fechaInicio, fechaFin));
    }
}