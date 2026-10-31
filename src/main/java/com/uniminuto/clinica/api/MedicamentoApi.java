package com.uniminuto.clinica.api;

import com.uniminuto.clinica.entity.Medicamento;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.models.EstadoRq;
import com.uniminuto.clinica.models.MedicamentoRq;
import com.uniminuto.clinica.models.MiRespuestaRS;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * API REST para la gestión de medicamentos.
 * Ruta base: {@code /clinica/v1/medicamento}. No existe operación de borrado:
 * los medicamentos se desactivan con {@code PATCH /{id}/estado}.
 */
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/medicamento")
public interface MedicamentoApi {

    /**
     * Lista todos los medicamentos (activos e inactivos).
     *
     * @return lista ordenada por nombre.
     */
    @GetMapping(value = "/listar", produces = {"application/json"})
    ResponseEntity<List<Medicamento>> listarMedicamentos() throws BadRequestException;

    /**
     * Lista solo los medicamentos activos (para recetar en fórmulas).
     *
     * @return lista de medicamentos activos.
     */
    @GetMapping(value = "/listar-activos", produces = {"application/json"})
    ResponseEntity<List<Medicamento>> listarMedicamentosActivos();

    /**
     * Consulta un medicamento por su identificador.
     *
     * @param id identificador del medicamento.
     * @return el medicamento (404 si no existe).
     */
    @GetMapping(value = "/{id}", produces = {"application/json"})
    ResponseEntity<Medicamento> buscarMedicamentoPorId(@PathVariable("id") Long id);

    /**
     * Crea un medicamento nuevo.
     *
     * @param medicamentoRq datos del medicamento.
     * @return confirmación (201); 400 si hay datos inválidos; 409 si el nombre ya existe.
     */
    @PostMapping(value = "/crear", produces = {"application/json"}, consumes = {"application/json"})
    ResponseEntity<MiRespuestaRS> crearMedicamento(@RequestBody MedicamentoRq medicamentoRq);

    /**
     * Edita un medicamento existente.
     *
     * @param id            identificador del medicamento.
     * @param medicamentoRq nuevos datos.
     * @return confirmación (200); 404 si no existe; 409 si el nombre es de otro medicamento.
     */
    @PutMapping(value = "/{id}", produces = {"application/json"}, consumes = {"application/json"})
    ResponseEntity<MiRespuestaRS> actualizarMedicamento(@PathVariable("id") Long id,
                                                        @RequestBody MedicamentoRq medicamentoRq);

    /**
     * Activa o desactiva un medicamento.
     *
     * @param id       identificador del medicamento.
     * @param estadoRq estado deseado.
     * @return confirmación (200); 409 si se intenta activar un medicamento vencido.
     */
    @PatchMapping(value = "/{id}/estado", produces = {"application/json"}, consumes = {"application/json"})
    ResponseEntity<MiRespuestaRS> cambiarEstadoMedicamento(@PathVariable("id") Long id,
                                                           @RequestBody EstadoRq estadoRq);
}
