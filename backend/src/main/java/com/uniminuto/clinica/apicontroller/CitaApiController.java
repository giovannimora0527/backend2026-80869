package com.uniminuto.clinica.apicontroller;

import com.uniminuto.clinica.api.CitaApi;
import com.uniminuto.clinica.entity.Cita;
import com.uniminuto.clinica.models.CitaResumenRs;
import com.uniminuto.clinica.models.CitaRq;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.models.PaginaRs;
import com.uniminuto.clinica.models.filtro.CitaFiltro;
import com.uniminuto.clinica.service.CitaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controlador REST de citas médicas. Solo traduce HTTP ↔ servicio: las reglas de negocio
 * viven en {@link CitaService} y los errores los traduce {@code GlobalExceptionHandler}.
 * <p>
 * Ruta base: {@code /clinica/v1/cita}. El contrato (rutas, verbos y anotaciones de
 * parámetros) está definido en {@link CitaApi}.
 * </p>
 */
@RestController
@RequestMapping("/cita")
public class CitaApiController implements CitaApi {

    /** Servicio con la lógica de negocio de las citas. */
    @Autowired
    private CitaService citaService;

    /**
     * Lista todas las citas registradas.
     *
     * @return respuesta 200 con la lista de citas.
     */
    @Override
    public ResponseEntity<List<Cita>> listarCitas() {
        return ResponseEntity.ok(citaService.listarCitas());
    }

    /**
     * Lista las citas con paginación, orden y filtros opcionales.
     *
     * @param filtro criterios de búsqueda, página, tamaño y orden.
     * @return respuesta 200 con la página de citas en formato resumido; el servicio
     *         responde 400 si el criterio de orden no está permitido.
     */
    @Override
    public ResponseEntity<PaginaRs<CitaResumenRs>> listarCitasPaginado(CitaFiltro filtro) {
        return ResponseEntity.ok(citaService.listarCitasPaginado(filtro));
    }

    /**
     * Consulta una cita por su identificador.
     *
     * @param id identificador de la cita.
     * @return respuesta 200 con la cita; el servicio responde 404 si no existe.
     */
    @Override
    public ResponseEntity<Cita> consultarCita(Long id) {
        return ResponseEntity.ok(citaService.consultarCita(id));
    }

    /**
     * Crea una cita nueva validando cliente, mascota, veterinario, fecha y
     * disponibilidad de agenda.
     *
     * @param rq datos de la cita a crear.
     * @return respuesta 201 con el mensaje y el id de la cita creada; el servicio
     *         responde 400 por datos inválidos o 409 por conflicto de agenda.
     */
    @Override
    public ResponseEntity<MiRespuestaRS> crearCita(CitaRq rq) {
        return ResponseEntity.status(HttpStatus.CREATED).body(citaService.crearCita(rq));
    }

    /**
     * Actualiza una cita existente según su estado actual (una cita atendida o
     * cancelada no se edita).
     *
     * @param id identificador de la cita a modificar.
     * @param rq nuevos datos de la cita.
     * @return respuesta 200 con el resultado; el servicio responde 400, 404 o 409
     *         según el caso.
     */
    @Override
    public ResponseEntity<MiRespuestaRS> actualizarCita(Long id, CitaRq rq) {
        return ResponseEntity.ok(citaService.actualizarCita(id, rq));
    }

    /**
     * Cancela una cita.
     *
     * @param id identificador de la cita.
     * @return respuesta 200 con el resultado; el servicio responde 404 si no existe
     *         o 409 si el estado actual no permite cancelarla.
     */
    @Override
    public ResponseEntity<MiRespuestaRS> cancelarCita(Long id) {
        return ResponseEntity.ok(citaService.cancelarCita(id));
    }

    /**
     * Reactiva una cita que estaba cancelada.
     *
     * @param id identificador de la cita.
     * @return respuesta 200 con el resultado; el servicio responde 404 si no existe
     *         o 409 si la cita no está cancelada.
     */
    @Override
    public ResponseEntity<MiRespuestaRS> reactivarCita(Long id) {
        return ResponseEntity.ok(citaService.reactivarCita(id));
    }
}