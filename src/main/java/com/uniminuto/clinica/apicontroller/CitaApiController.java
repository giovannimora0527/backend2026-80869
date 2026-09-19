package com.uniminuto.clinica.apicontroller;

import com.uniminuto.clinica.api.CitaApi;
import com.uniminuto.clinica.models.CitaRQ;
import com.uniminuto.clinica.models.CitaRS;
import com.uniminuto.clinica.service.CitaService;
import com.uniminuto.clinica.exception.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;


@RestController
public class CitaApiController implements CitaApi {



    @Autowired private CitaService citaService;

    @Override
    public ResponseEntity<List<CitaRS>> listarCitas(LocalDateTime fechaInicial, LocalDateTime fechaFinal) throws BadRequestException {
        return ResponseEntity.ok(citaService.filtrarPorRangoFechas(fechaInicial, fechaFinal));
    }

    @Override
    public ResponseEntity<CitaRS> guardarCita(CitaRQ request) throws BadRequestException {
        return ResponseEntity.ok(citaService.crear(request));
    }

    @Override
    public ResponseEntity<CitaRS> actualizarCita(CitaRQ request) throws BadRequestException {
        return ResponseEntity.ok(citaService.actualizar(request));
    }
}

