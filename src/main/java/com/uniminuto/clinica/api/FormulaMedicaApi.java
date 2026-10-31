package com.uniminuto.clinica.api;

import com.uniminuto.clinica.entity.FormulaMedica;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.models.EstadoRq;
import com.uniminuto.clinica.models.FiltroPaginadoRq;
import com.uniminuto.clinica.models.FormulaResumenRs;
import com.uniminuto.clinica.models.FormulaRq;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.models.PageRs;
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

import java.util.List;

/**
 * Contrato REST de las fórmulas médicas ({@code /formula-medica}).
 */
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/formula-medica")
public interface FormulaMedicaApi {

    /**
     * Lista todas las fórmulas (sin paginar), de la más reciente a la más antigua.
     *
     * @return lista de fórmulas.
     * @throws BadRequestException error de validación.
     */
    @GetMapping(value = "/listar", produces = {"application/json"})
    ResponseEntity<List<FormulaMedica>> listarFormulas() throws BadRequestException;

    /**
     * Listado paginado para el DataTable (información resumida). Parámetros opcionales: page, size,
     * sort (fechaCreacion, fechaEmision, mascota, cliente, veterinario, medicamento, estado), dir, q,
     * clienteId, mascotaId, medicoId, medicamentoId, activo, desde y hasta.
     *
     * @param filtro parámetros de la consulta.
     * @return página de fórmulas resumidas.
     */
    @GetMapping(value = "/paginado", produces = {"application/json"})
    ResponseEntity<PageRs<FormulaResumenRs>> listarFormulasPaginado(@ModelAttribute FiltroPaginadoRq filtro);

    /**
     * Consulta el detalle completo de una fórmula.
     *
     * @param id identificador de la fórmula.
     * @return la fórmula con todos sus datos.
     */
    @GetMapping(value = "/{id}", produces = {"application/json"})
    ResponseEntity<FormulaMedica> buscarFormulaPorId(@PathVariable("id") Long id);

    /**
     * Registra una fórmula médica.
     *
     * @param formulaRq datos de la fórmula.
     * @return confirmación con código 201.
     * @throws BadRequestException si los datos no son válidos.
     */
    @PostMapping(value = "/guardar", produces = {"application/json"}, consumes = {"application/json"})
    ResponseEntity<MiRespuestaRS> guardarFormula(@RequestBody FormulaRq formulaRq) throws BadRequestException;

    /**
     * Edita una fórmula (ruta anterior, el id viaja en el cuerpo). Se conserva por compatibilidad.
     *
     * @param formulaRq datos nuevos con su id.
     * @return confirmación.
     * @throws BadRequestException si los datos no son válidos.
     */
    @PostMapping(value = "/actualizar", produces = {"application/json"}, consumes = {"application/json"})
    ResponseEntity<MiRespuestaRS> actualizarFormula(@RequestBody FormulaRq formulaRq) throws BadRequestException;

    /**
     * Edita una fórmula (el id viaja en la URL).
     *
     * @param id        identificador de la fórmula.
     * @param formulaRq datos nuevos.
     * @return confirmación.
     */
    @PutMapping(value = "/{id}", produces = {"application/json"}, consumes = {"application/json"})
    ResponseEntity<MiRespuestaRS> editarFormula(@PathVariable("id") Long id, @RequestBody FormulaRq formulaRq);

    /**
     * Activa o inactiva una fórmula (no existe eliminación física).
     *
     * @param id       identificador de la fórmula.
     * @param estadoRq nuevo estado.
     * @return confirmación.
     */
    @PatchMapping(value = "/{id}/estado", produces = {"application/json"}, consumes = {"application/json"})
    ResponseEntity<MiRespuestaRS> cambiarEstadoFormula(@PathVariable("id") Long id, @RequestBody EstadoRq estadoRq);
}
