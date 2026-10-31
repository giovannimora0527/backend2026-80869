package com.uniminuto.clinica.api;

import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.models.AnotacionHistoriaRq;
import com.uniminuto.clinica.models.AnotacionHistoriaRs;
import com.uniminuto.clinica.models.FiltroPaginadoRq;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.models.PageRs;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Contrato REST de las anotaciones de historia clínica ({@code /anotacion-historia}).
 */
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/anotacion-historia")
public interface AnotacionHistoriaApi {

    /**
     * Agrega una anotación a una historia clínica existente.
     *
     * @param anotacionHistoria datos de la anotación.
     * @return confirmación con código 201.
     * @throws BadRequestException si los datos no son válidos.
     */
    @PostMapping(value = "/crear", produces = {"application/json"}, consumes = {"application/json"})
    ResponseEntity<MiRespuestaRS> crearAnotacionHistoria(@RequestBody AnotacionHistoriaRq anotacionHistoria)
            throws BadRequestException;

    /**
     * Histórico paginado de anotaciones de una historia. Parámetros opcionales: page, size,
     * sort (fecha, veterinario, descripcion, diagnostico, observaciones), dir, q, medicoId, desde y hasta.
     *
     * @param historiaId identificador de la historia.
     * @param filtro     parámetros de la consulta.
     * @return página de anotaciones, de la más reciente a la más antigua por defecto.
     */
    @GetMapping(value = "/por-historia/{historiaId}", produces = {"application/json"})
    ResponseEntity<PageRs<AnotacionHistoriaRs>> listarPorHistoria(@PathVariable("historiaId") Long historiaId,
                                                                  @ModelAttribute FiltroPaginadoRq filtro);

    /**
     * Histórico paginado de las anotaciones de una mascota (todas sus historias).
     *
     * @param mascotaId identificador de la mascota.
     * @param filtro    parámetros de la consulta.
     * @return página de anotaciones, de la más reciente a la más antigua por defecto.
     */
    @GetMapping(value = "/por-mascota/{mascotaId}", produces = {"application/json"})
    ResponseEntity<PageRs<AnotacionHistoriaRs>> listarPorMascota(@PathVariable("mascotaId") Integer mascotaId,
                                                                 @ModelAttribute FiltroPaginadoRq filtro);

    /**
     * Consulta el detalle de una anotación.
     *
     * @param id identificador de la anotación.
     * @return la anotación con las fórmulas de su consulta.
     */
    @GetMapping(value = "/{id}", produces = {"application/json"})
    ResponseEntity<AnotacionHistoriaRs> buscarPorId(@PathVariable("id") Long id);
}
