package com.uniminuto.clinica.apicontroller;

import com.uniminuto.clinica.api.HistoriaApi;
import com.uniminuto.clinica.entity.AnotacionHistoria;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.models.AnotacionHistoriaRequest;
import com.uniminuto.clinica.models.HistoriaConAnotacionesRS;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.service.HistoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Controlador REST para manejar los endpoints de historias medicas.
 */
@RestController
public class HistoriaApiController implements HistoriaApi {

    @Autowired
    private HistoriaService historiaService;

    @Override
    public ResponseEntity<AnotacionHistoria> crearAnotacion(AnotacionHistoriaRequest request) throws BadRequestException {
        return ResponseEntity.ok(historiaService.crearAnotacion(request));
    }

    @Override
    public ResponseEntity<AnotacionHistoria> actualizarAnotacion(Integer id, AnotacionHistoriaRequest request) throws BadRequestException {
        return ResponseEntity.ok(historiaService.actualizarAnotacion(id, request));
    }

    @Override
    public ResponseEntity<List<HistoriaConAnotacionesRS>> listarHistorias(Integer historiaId, LocalDateTime fechaInicial, LocalDateTime fechaFinal) throws BadRequestException {
        return ResponseEntity.ok(historiaService.listarHistorias(historiaId, fechaInicial, fechaFinal));
    }

    @Override
    public ResponseEntity<MiRespuestaRS> eliminarAnotacion(Integer id) throws BadRequestException {
        return ResponseEntity.ok(historiaService.eliminarAnotacion(id));
    }

    @Override
    public ResponseEntity<MiRespuestaRS> eliminarHistoria(Integer id) throws BadRequestException {
        return ResponseEntity.ok(historiaService.eliminarHistoria(id));
    }
}
