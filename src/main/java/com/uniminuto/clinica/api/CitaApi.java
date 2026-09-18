package com.uniminuto.clinica.api;

import com.uniminuto.clinica.entity.Cita;
import com.uniminuto.clinica.models.CitaRequest;
import com.uniminuto.clinica.models.MiRespuestaRS;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/cita")
public interface CitaApi {

    /**
     * Lista todas las citas registradas en el sistema.
     *
     * @return lista de todas las citas.
     * @throws BadRequestException excepcion.
     */
    @GetMapping(value = "/listar",
            produces = {"application/json"})
    ResponseEntity<List<Cita>> listarCitas()
            throws BadRequestException;

    /**
     * Filtra las citas registradas entre dos fechas.
     *
     * @param fechaInicial limite inferior del rango de fechas.
     * @param fechaFinal limite superior del rango de fechas.
     * @return lista de citas ordenadas de la mas reciente a la mas antigua.
     * @throws BadRequestException excepcion.
     */
    @GetMapping(value = "/citas",
            produces = {"application/json"})
    ResponseEntity<List<Cita>> filtrarCitas(
            @RequestParam LocalDateTime fechaInicial,
            @RequestParam LocalDateTime fechaFinal)
            throws BadRequestException;

    /**
     * Crea una nueva cita en el sistema.
     *
     * @param citaRequest datos de la cita a crear.
     * @return la cita creada, con su id asignado.
     * @throws BadRequestException si el cliente, mascota o medico no existen.
     */
    @PostMapping(value = "/crear",
            produces = {"application/json"},
            consumes = {"application/json"})
    ResponseEntity<Cita> crearCita(@RequestBody CitaRequest citaRequest)
            throws BadRequestException;

    /**
     * Actualiza una cita existente.
     *
     * @param id           id de la cita a actualizar.
     * @param citaRequest  datos nuevos de la cita.
     * @return la cita actualizada.
     * @throws BadRequestException si la cita, cliente, mascota o medico no existen.
     */
    @PutMapping(value = "/actualizar/{id}",
            produces = {"application/json"},
            consumes = {"application/json"})
    ResponseEntity<Cita> actualizarCita(@PathVariable Integer id, @RequestBody CitaRequest citaRequest)
            throws BadRequestException;

    /**
     * Elimina una cita existente.
     *
     * @param id id de la cita a eliminar.
     * @return un mensaje indicando que la cita fue eliminada.
     * @throws BadRequestException si la cita no existe.
     */
    @DeleteMapping(value = "/eliminar/{id}",
            produces = {"application/json"})
    ResponseEntity<MiRespuestaRS> eliminarCita(@PathVariable Integer id)
            throws BadRequestException;
}
