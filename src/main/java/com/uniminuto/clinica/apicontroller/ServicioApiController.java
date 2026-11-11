package com.uniminuto.clinica.apicontroller;

import com.uniminuto.clinica.api.ServicioApi;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.models.LogTarifaRs;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.models.ServicioRq;
import com.uniminuto.clinica.models.ServicioRs;
import com.uniminuto.clinica.service.ServicioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ServicioApiController implements ServicioApi {

    @Autowired
    private ServicioService servicioService;

    @Override
    public ResponseEntity<List<ServicioRs>> listar() throws BadRequestException {
        return ResponseEntity.ok(this.servicioService.listar());
    }

    @Override
    public ResponseEntity<List<ServicioRs>> listarActivos() throws BadRequestException {
        return ResponseEntity.ok(this.servicioService.listarActivos());
    }

    @Override
    public ResponseEntity<MiRespuestaRS> guardar(ServicioRq servicioRq) throws BadRequestException {
        return ResponseEntity.ok(this.servicioService.crear(servicioRq));
    }

    @Override
    public ResponseEntity<MiRespuestaRS> actualizar(ServicioRq servicioRq) throws BadRequestException {
        return ResponseEntity.ok(this.servicioService.actualizar(servicioRq));
    }

    @Override
    public ResponseEntity<List<LogTarifaRs>> historialTarifas(Long servicioId) throws BadRequestException {
        return ResponseEntity.ok(this.servicioService.historialTarifas(servicioId));
    }
}
