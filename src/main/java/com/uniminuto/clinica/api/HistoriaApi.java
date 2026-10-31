package com.uniminuto.clinica.api;

import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.models.FiltroPaginadoRq;
import com.uniminuto.clinica.models.HistoriaMedicaRq;
import com.uniminuto.clinica.models.HistoriaMedicaRs;
import com.uniminuto.clinica.models.HistoriaResumenRs;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.models.PageRs;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * Contrato REST de las historias clínicas ({@code /historia-clinica}).
 */
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/historia-clinica")
public interface HistoriaApi {

    /**
     * Lista todas las historias con anotaciones y fórmulas (sin paginar).
     *
     * @return historias con su detalle.
     * @throws BadRequestException error de validación.
     */
    @GetMapping(value = "/listar", produces = {"application/json"})
    ResponseEntity<List<HistoriaMedicaRs>> listarAllHIstorias() throws BadRequestException;

    /**
     * Listado paginado para el DataTable. Parámetros opcionales: page, size, sort
     * (fecha, mascota, cliente, veterinario, motivo, estado), dir, q, estado, clienteId,
     * mascotaId, medicoId, desde y hasta.
     *
     * @param filtro parámetros de la consulta.
     * @return página de historias resumidas.
     */
    @GetMapping(value = "/paginado", produces = {"application/json"})
    ResponseEntity<PageRs<HistoriaResumenRs>> listarHistoriasPaginado(@ModelAttribute FiltroPaginadoRq filtro);

    /**
     * Consulta el detalle de una historia.
     *
     * @param id identificador de la historia.
     * @return historia con anotaciones y fórmulas.
     */
    @GetMapping(value = "/{id}", produces = {"application/json"})
    ResponseEntity<HistoriaMedicaRs> buscarHistoriaPorId(@PathVariable("id") Long id);

    /**
     * Registra una historia clínica.
     *
     * @param historiaMedicaRq datos de la consulta.
     * @return confirmación con código 201.
     * @throws BadRequestException si los datos no son válidos.
     */
    @PostMapping(value = "/guardar", produces = {"application/json"}, consumes = {"application/json"})
    ResponseEntity<MiRespuestaRS> guardarHistoria(@RequestBody HistoriaMedicaRq historiaMedicaRq)
            throws BadRequestException;

    /**
     * Edita una historia (ruta anterior, el id viaja en el cuerpo). Se conserva por compatibilidad.
     *
     * @param historiaMedicaRq datos nuevos con su id.
     * @return confirmación.
     * @throws BadRequestException si los datos no son válidos.
     */
    @PostMapping(value = "/actualizar", produces = {"application/json"}, consumes = {"application/json"})
    ResponseEntity<MiRespuestaRS> actualizarHistoria(@RequestBody HistoriaMedicaRq historiaMedicaRq)
            throws BadRequestException;

    /**
     * Edita una historia (el id viaja en la URL).
     *
     * @param id               identificador de la historia.
     * @param historiaMedicaRq datos nuevos.
     * @return confirmación.
     */
    @PutMapping(value = "/{id}", produces = {"application/json"}, consumes = {"application/json"})
    ResponseEntity<MiRespuestaRS> editarHistoria(@PathVariable("id") Long id,
                                                 @RequestBody HistoriaMedicaRq historiaMedicaRq);
}
