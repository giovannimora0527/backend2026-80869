package com.uniminuto.clinica.apicontroller;

import com.uniminuto.clinica.api.CitaApi;
import com.uniminuto.clinica.entity.Cita;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.models.CitaRq;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.service.CitaService;
import com.uniminuto.clinica.serviceimpl.AgendaCitasServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
public class CitaApiController implements CitaApi {

    private final CitaService citaService;
    private final AgendaCitasServiceImpl agenda;
    private final HttpServletRequest request;

    public CitaApiController(
            CitaService citaService,
            AgendaCitasServiceImpl agenda,
            HttpServletRequest request
    ) {
        this.citaService = citaService;
        this.agenda = agenda;
        this.request = request;
    }

    private String autorizacion() {
        return request.getHeader(HttpHeaders.AUTHORIZATION);
    }

    @Override
    public ResponseEntity<List<Cita>> listarCitas() {
        return ResponseEntity.ok(agenda.listar(autorizacion()));
    }

    @Override
    public ResponseEntity<List<Cita>> listarCitasRango(
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin
    ) {
        return ResponseEntity.ok(
                agenda.listarRango(autorizacion(), fechaInicio, fechaFin)
        );
    }

    @Override
    public ResponseEntity<List<Cita>> listarCitasPorMascota(Integer mascotaId) {
        return ResponseEntity.ok(
                agenda.listarPorMascota(autorizacion(), mascotaId)
        );
    }

    @Override
    public ResponseEntity<MiRespuestaRS> crearCita(CitaRq cita)
            throws BadRequestException {
        return ResponseEntity.ok(citaService.crearCita(cita));
    }

    @Override
    public ResponseEntity<MiRespuestaRS> actualizarCita(CitaRq cita)
            throws BadRequestException {
        return ResponseEntity.ok(citaService.actualizarCita(cita));
    }

    @PostMapping(value = "/{id}/finalizar-atencion", consumes = "application/json")
    public ResponseEntity<Void> finalizarAtencion(
            @PathVariable("id") Long id,
            @RequestBody AgendaCitasServiceImpl.DatosAtencion datos
    ) {
        agenda.finalizarAtencion(autorizacion(), id, datos);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> responderErrorDeAcceso(
            ResponseStatusException ex
    ) {
        String mensaje = ex.getReason() == null
                ? "Acceso denegado."
                : ex.getReason();

        return ResponseEntity.status(ex.getStatusCode()).body(Map.of(
                "status", ex.getStatusCode().value(),
                "message", mensaje
        ));
    }
}