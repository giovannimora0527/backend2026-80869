package com.uniminuto.clinica.api;

import com.uniminuto.clinica.entity.Cliente;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.models.ClienteRq;
import com.uniminuto.clinica.models.EstadoRq;
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
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * API REST para la gestión de clientes.
 * Ruta base: {@code /clinica/v1/cliente}. No existe operación de borrado: los
 * clientes se desactivan con {@code PATCH /{id}/estado}.
 */
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/cliente")
public interface ClienteApi {

    /**
     * Lista todos los clientes (activos e inactivos).
     *
     * @return lista de clientes ordenada por nombre.
     */
    @GetMapping(value = "/listar", produces = {"application/json"})
    ResponseEntity<List<Cliente>> listarClientes() throws BadRequestException;

    /**
     * Lista solo los clientes activos (para combos de otros módulos).
     *
     * @return lista de clientes activos.
     */
    @GetMapping(value = "/listar-activos", produces = {"application/json"})
    ResponseEntity<List<Cliente>> listarClientesActivos();

    /**
     * Busca un cliente por su número de documento.
     *
     * @param documento número de documento.
     * @return el cliente (404 si no existe).
     */
    @GetMapping(value = "/buscar-por-documento", produces = {"application/json"})
    ResponseEntity<Cliente> buscarClientesPorDocumento(@RequestParam String documento)
            throws BadRequestException;

    /**
     * Consulta un cliente por su identificador.
     *
     * @param id identificador del cliente.
     * @return el cliente (404 si no existe).
     */
    @GetMapping(value = "/{id}", produces = {"application/json"})
    ResponseEntity<Cliente> buscarClientePorId(@PathVariable("id") Long id);

    /**
     * Crea un cliente nuevo.
     *
     * @param clienteRq datos del cliente.
     * @return confirmación (201); 400 si hay datos inválidos; 409 si el documento ya existe.
     */
    @PostMapping(value = "/crear", produces = {"application/json"}, consumes = {"application/json"})
    ResponseEntity<MiRespuestaRS> crearCliente(@RequestBody ClienteRq clienteRq);

    /**
     * Edita un cliente existente.
     *
     * @param id        identificador del cliente.
     * @param clienteRq nuevos datos.
     * @return confirmación (200); 404 si no existe; 409 si el documento es de otro cliente.
     */
    @PutMapping(value = "/{id}", produces = {"application/json"}, consumes = {"application/json"})
    ResponseEntity<MiRespuestaRS> actualizarCliente(@PathVariable("id") Long id,
                                                    @RequestBody ClienteRq clienteRq);

    /**
     * Activa o desactiva un cliente.
     *
     * @param id       identificador del cliente.
     * @param estadoRq estado deseado.
     * @return confirmación (200); 409 si se intenta desactivar con citas vivas.
     */
    @PatchMapping(value = "/{id}/estado", produces = {"application/json"}, consumes = {"application/json"})
    ResponseEntity<MiRespuestaRS> cambiarEstadoCliente(@PathVariable("id") Long id,
                                                       @RequestBody EstadoRq estadoRq);
}
