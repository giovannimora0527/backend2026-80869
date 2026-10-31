package com.uniminuto.clinica.apicontroller;

import com.uniminuto.clinica.api.CitaApi;
import com.uniminuto.clinica.entity.Cita;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.models.CambioEstadoCitaRq;
import com.uniminuto.clinica.models.CitaRq;
import com.uniminuto.clinica.models.CitaRs;
import com.uniminuto.clinica.models.FiltroPaginadoRq;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.models.PageRs;
import com.uniminuto.clinica.service.CitaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Controlador REST de citas. Solo recibe la petición y delega en {@link CitaService};
 * no contiene reglas de negocio.
 */
@RestController
public class CitaApiController implements CitaApi {

    private final CitaService citaService;

    /**
     * Crea el controlador con su servicio.
     *
     * @param citaService servicio de citas.
     */
    public CitaApiController(CitaService citaService) {
        this.citaService = citaService;
    }

    @Override
    public ResponseEntity<List<Cita>> listarCitas() throws BadRequestException {
        return ResponseEntity.ok(citaService.listarCitas());
    }

    @Override
    public ResponseEntity<PageRs<CitaRs>> listarCitasPaginado(FiltroPaginadoRq filtro) {
        return ResponseEntity.ok(citaService.listarCitasPaginado(filtro));
    }

    @Override
    public ResponseEntity<List<Cita>> listarCitasRango(LocalDateTime fechaInicio, LocalDateTime fechaFin)
            throws BadRequestException {
        return ResponseEntity.ok(citaService.listarCitasRango(fechaInicio, fechaFin));
    }

    @Override
    public ResponseEntity<List<Cita>> listarCitasPorMascota(Integer mascotaId) throws BadRequestException {
        return ResponseEntity.ok(citaService.listarCitasPorMascota(mascotaId));
    }

    @Override
    public ResponseEntity<Cita> buscarCitaPorId(Long id) {
        return ResponseEntity.ok(citaService.buscarCitaPorId(id));
    }

    @Override
    public ResponseEntity<MiRespuestaRS> crearCita(CitaRq cita) throws BadRequestException {
        return ResponseEntity.status(HttpStatus.CREATED).body(citaService.crearCita(cita));
    }

    @Override
    public ResponseEntity<MiRespuestaRS> actualizarCita(CitaRq cita) throws BadRequestException {
        return ResponseEntity.ok(citaService.actualizarCita(cita));
    }

    @Override
    public ResponseEntity<MiRespuestaRS> editarCita(Long id, CitaRq cita) {
        cita.setId(id);
        return ResponseEntity.ok(citaService.actualizarCita(cita));
    }

    @Override
    public ResponseEntity<MiRespuestaRS> cambiarEstadoCita(Long id, CambioEstadoCitaRq request) {
        String estado = request == null ? null : request.getEstado();
        return ResponseEntity.ok(citaService.cambiarEstadoCita(id, estado));
    }

    @Override
    public ResponseEntity<MiRespuestaRS> reactivarCita(Long id) {
        return ResponseEntity.ok(citaService.reactivarCita(id));
    }
}
