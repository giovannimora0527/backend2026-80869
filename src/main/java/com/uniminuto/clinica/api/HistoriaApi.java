package com.uniminuto.clinica.api;

import com.uniminuto.clinica.entity.AnotacionHistoria;
import com.uniminuto.clinica.models.AnotacionHistoriaRequest;
import com.uniminuto.clinica.models.HistoriaConAnotacionesRS;
import com.uniminuto.clinica.models.MiRespuestaRS;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/historia")
public interface HistoriaApi {

    /**
     * Crea una nueva anotación en la historia médica.
     * Si el paciente no tiene historia médica, se crea automáticamente.
     *
     * @param request payload con la informacion de la mascota, medico y descripcion.
     * @return la anotacion creada.
     * @throws BadRequestException si los campos requeridos no estan presentes o no existe la mascota/medico.
     */
    @PostMapping(value = "/anotacion/crear", produces = {"application/json"}, consumes = {"application/json"})
    ResponseEntity<AnotacionHistoria> crearAnotacion(@RequestBody AnotacionHistoriaRequest request) throws BadRequestException;

    /**
     * Actualiza una anotación existente en la historia médica.
     *
     * @param id el ID de la anotación a actualizar.
     * @param request payload con la nueva descripcion y/o medico.
     * @return la anotacion actualizada.
     * @throws BadRequestException si la anotación no existe o los campos están vacios.
     */
    @PutMapping(value = "/anotacion/actualizar/{id}", produces = {"application/json"}, consumes = {"application/json"})
    ResponseEntity<AnotacionHistoria> actualizarAnotacion(@PathVariable Integer id, @RequestBody AnotacionHistoriaRequest request) throws BadRequestException;

    /**
     * Lista las historias medicas y sus anotaciones de manera dinámica.
     * Permite filtrar por una historia en particular y/o por un rango de fechas.
     *
     * @param historiaId el ID de la historia (opcional).
     * @param fechaInicial la fecha inferior del rango (opcional).
     * @param fechaFinal la fecha superior del rango (opcional).
     * @return lista de historias con sus respectivas anotaciones ordenadas.
     * @throws BadRequestException si solo se proporciona una fecha del rango o la fecha inicial es mayor a la final.
     */
    @GetMapping(value = "/listar", produces = {"application/json"})
    ResponseEntity<List<HistoriaConAnotacionesRS>> listarHistorias(
            @RequestParam(required = false) Integer historiaId,
            @RequestParam(required = false) LocalDateTime fechaInicial,
            @RequestParam(required = false) LocalDateTime fechaFinal
    ) throws BadRequestException;

    /**
     * Elimina una anotación específica de la base de datos.
     *
     * @param id el ID de la anotación.
     * @return respuesta de exito.
     * @throws BadRequestException si la anotacion no existe.
     */
    @DeleteMapping(value = "/anotacion/eliminar/{id}", produces = {"application/json"})
    ResponseEntity<MiRespuestaRS> eliminarAnotacion(@PathVariable Integer id) throws BadRequestException;

    /**
     * Elimina una historia médica completa y todas sus anotaciones en cascada.
     *
     * @param id el ID de la historia medica.
     * @return respuesta de exito.
     * @throws BadRequestException si la historia no existe.
     */
    @DeleteMapping(value = "/eliminar/{id}", produces = {"application/json"})
    ResponseEntity<MiRespuestaRS> eliminarHistoria(@PathVariable Integer id) throws BadRequestException;
}
