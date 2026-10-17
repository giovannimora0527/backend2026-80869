package com.uniminuto.clinica.api;

import com.uniminuto.clinica.entity.Cliente;
import com.uniminuto.clinica.models.MiRespuestaRS;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrato API REST para la gestión de Clientes (Propietarios de mascotas).
 *
 * @author Yulian Estephania Montealegre
 */
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/cliente")
public interface ClienteApi {

    /**
     * Endpoint para listar todos los clientes registrados.
     *
     * @return Lista de clientes.
     */
    @GetMapping(value = "/listar", produces = {"application/json"})
    ResponseEntity<List<Cliente>> listarClientes();

    /**
     * Endpoint para buscar un cliente por su ID.
     *
     * @param id ID del cliente.
     * @return Entidad del cliente encontrado.
     */
    @GetMapping(value = "/buscar/{id}", produces = {"application/json"})
    ResponseEntity<Cliente> buscarClientePorId(@PathVariable Long id);

    /**
     * Endpoint para guardar un nuevo cliente.
     *
     * @param cliente Objeto cliente con la información.
     * @return Respuesta estandarizada.
     */
    @PostMapping(value = "/guardar", produces = {"application/json"}, consumes = {"application/json"})
    ResponseEntity<MiRespuestaRS> guardarCliente(@RequestBody Cliente cliente);
}