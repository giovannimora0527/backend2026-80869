package com.uniminuto.clinica.api;

import com.uniminuto.clinica.entity.Cita;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.models.CambioEstadoCitaRq;
import com.uniminuto.clinica.models.CitaRq;
import com.uniminuto.clinica.models.CitaRs;
import com.uniminuto.clinica.models.FiltroPaginadoRq;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.models.PageRs;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Contrato REST de las citas médicas ({@code /cita}).
 */
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/cita")
public interface CitaApi {

    /**
     * Lista todas las citas (sin paginar), de la más reciente a la más antigua.
     *
     * @return lista de citas.
     * @throws BadRequestException error de validación.
     */
    @GetMapping(value = "/listar", produces = {"application/json"})
    ResponseEntity<List<Cita>> listarCitas() throws BadRequestException;

    /**
     * Listado paginado para el DataTable. Parámetros opcionales: page, size, sort
     * (fecha, hora, cliente, mascota, veterinario, estado, motivo), dir, q, estado,
     * clienteId, mascotaId, medicoId, desde y hasta.
     *
     * @param filtro parámetros de la consulta.
     * @return página de citas resumidas.
     */
    @GetMapping(value = "/paginado", produces = {"application/json"})
    ResponseEntity<PageRs<CitaRs>> listarCitasPaginado(@ModelAttribute FiltroPaginadoRq filtro);

    /**
     * Lista las citas cuya fecha está en un rango.
     *
     * @param fechaInicio inicio del rango.
     * @param fechaFin    fin del rango.
     * @return citas del rango.
     * @throws BadRequestException si las fechas no son válidas.
     */
    @GetMapping(value = "/listar-rango", produces = {"application/json"})
    ResponseEntity<List<Cita>> listarCitasRango(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin)
            throws BadRequestException;

    /**
     * Lista las citas de una mascota.
     *
     * @param mascotaId id de la mascota.
     * @return citas de la mascota ordenadas por fecha descendente.
     * @throws BadRequestException si el id no es válido.
     */
    @GetMapping(value = "/listar-por-mascota", produces = {"application/json"})
    ResponseEntity<List<Cita>> listarCitasPorMascota(@RequestParam Integer mascotaId)
            throws BadRequestException;

    /**
     * Consulta una cita por su id.
     *
     * @param id identificador de la cita.
     * @return la cita completa.
     */
    @GetMapping(value = "/{id}", produces = {"application/json"})
    ResponseEntity<Cita> buscarCitaPorId(@PathVariable("id") Long id);

    /**
     * Crea una cita.
     *
     * @param cita datos de la cita.
     * @return confirmación con código 201.
     * @throws BadRequestException si los datos no son válidos.
     */
    @PostMapping(value = "/crear", produces = {"application/json"}, consumes = {"application/json"})
    ResponseEntity<MiRespuestaRS> crearCita(@RequestBody CitaRq cita) throws BadRequestException;

    /**
     * Edita una cita (ruta anterior, el id viaja en el cuerpo). Se conserva por compatibilidad.
     *
     * @param cita datos de la cita con su id.
     * @return confirmación.
     * @throws BadRequestException si los datos no son válidos.
     */
    @PostMapping(value = "/actualizar", produces = {"application/json"}, consumes = {"application/json"})
    ResponseEntity<MiRespuestaRS> actualizarCita(@RequestBody CitaRq cita) throws BadRequestException;

    /**
     * Edita una cita (el id viaja en la URL).
     *
     * @param id   identificador de la cita.
     * @param cita datos nuevos.
     * @return confirmación.
     */
    @PutMapping(value = "/{id}", produces = {"application/json"}, consumes = {"application/json"})
    ResponseEntity<MiRespuestaRS> editarCita(@PathVariable("id") Long id, @RequestBody CitaRq cita);

    /**
     * Cambia solo el estado de la cita (confirmar, atender, cancelar...).
     *
     * @param id      identificador de la cita.
     * @param request nuevo estado.
     * @return confirmación.
     */
    @PatchMapping(value = "/{id}/estado", produces = {"application/json"}, consumes = {"application/json"})
    ResponseEntity<MiRespuestaRS> cambiarEstadoCita(@PathVariable("id") Long id,
                                                    @RequestBody CambioEstadoCitaRq request);

    /**
     * Reactiva una cita cancelada (operación explícita).
     *
     * @param id identificador de la cita cancelada.
     * @return confirmación.
     */
    @PatchMapping(value = "/{id}/reactivar", produces = {"application/json"})
    ResponseEntity<MiRespuestaRS> reactivarCita(@PathVariable("id") Long id);
}
