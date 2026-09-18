package com.uniminuto.clinica.apicontroller;

import com.uniminuto.clinica.api.CitaApi;
import com.uniminuto.clinica.entity.Cita;
import com.uniminuto.clinica.models.CitaRequest;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.service.CitaService;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
public class CitaApiController implements CitaApi {

    @Autowired
    private CitaService citaService;

    @Override
    public ResponseEntity<List<Cita>> listarCitas() throws BadRequestException {
        return ResponseEntity.ok(citaService.listarCitas());
    }

    @Override
    public ResponseEntity<List<Cita>> filtrarCitas(LocalDateTime fechaInicial, LocalDateTime fechaFinal)
            throws BadRequestException {
        return ResponseEntity.ok(citaService.filtrarCitas(fechaInicial, fechaFinal));
    }

    @Override
    public ResponseEntity<Cita> crearCita(CitaRequest citaRequest) throws BadRequestException {
        return ResponseEntity.ok(this.citaService.crearCita(citaRequest));
    }

    @Override
    public ResponseEntity<Cita> actualizarCita(Integer id, CitaRequest citaRequest) throws BadRequestException {
        return ResponseEntity.ok(this.citaService.actualizarCita(id, citaRequest));
    }

    @Override
    public ResponseEntity<MiRespuestaRS> eliminarCita(Integer id) throws BadRequestException {
        return ResponseEntity.ok(this.citaService.eliminarCita(id));
    }
}
