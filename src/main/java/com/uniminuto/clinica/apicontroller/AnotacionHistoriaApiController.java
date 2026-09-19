package com.uniminuto.clinica.apicontroller;

import com.uniminuto.clinica.api.AnotacionHistoriaApi;
import com.uniminuto.clinica.models.AnotacionHistoriaRQ;
import com.uniminuto.clinica.models.AnotacionHistoriaRS;
import com.uniminuto.clinica.service.AnotacionHistoriaService;
import com.uniminuto.clinica.exception.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

/**
 * recibe las peticiones HTTP
 * y delega la lógica de negocio al Servicio
 */
@RestController
public class AnotacionHistoriaApiController implements AnotacionHistoriaApi {


    @Autowired
    private AnotacionHistoriaService anotacionHistoriaService;


    @Override
    public ResponseEntity<AnotacionHistoriaRS> guardarAnotacion(AnotacionHistoriaRQ request) throws BadRequestException {
        return ResponseEntity.ok(anotacionHistoriaService.crear(request));
    }


    @Override
    public ResponseEntity<List<AnotacionHistoriaRS>> listarPorHistoria(Integer historiaId) throws BadRequestException {
        return ResponseEntity.ok(anotacionHistoriaService.listarPorHistoria(historiaId));
    }


    @Override
    public ResponseEntity<List<AnotacionHistoriaRS>> listarPorRangoFechas(LocalDateTime fechaInicial, LocalDateTime fechaFinal) throws BadRequestException {
        return ResponseEntity.ok(anotacionHistoriaService.listarPorRangoFechas(fechaInicial, fechaFinal));
    }


    @Override
    public ResponseEntity<AnotacionHistoriaRS> actualizarAnotacion(AnotacionHistoriaRQ request) throws BadRequestException {
        return ResponseEntity.ok(anotacionHistoriaService.actualizar(request));
    }
}