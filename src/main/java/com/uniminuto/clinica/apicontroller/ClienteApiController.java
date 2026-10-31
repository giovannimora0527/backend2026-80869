package com.uniminuto.clinica.apicontroller;

import com.uniminuto.clinica.api.ClienteApi;
import com.uniminuto.clinica.entity.Cliente;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.models.ClienteRq;
import com.uniminuto.clinica.models.EstadoRq;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.service.ClienteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controlador REST de clientes. Solo recibe la petición y delega en
 * {@link ClienteService}; no contiene reglas de negocio.
 * (Antes estaba en el paquete {@code api}; se movió a {@code apicontroller}
 * para seguir la misma organización que el resto de controladores.)
 */
@RestController
public class ClienteApiController implements ClienteApi {

    private final ClienteService clienteService;

    /**
     * Crea el controlador con su servicio.
     *
     * @param clienteService servicio de clientes.
     */
    public ClienteApiController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @Override
    public ResponseEntity<List<Cliente>> listarClientes() throws BadRequestException {
        return ResponseEntity.ok(clienteService.listarClientes());
    }

    @Override
    public ResponseEntity<List<Cliente>> listarClientesActivos() {
        return ResponseEntity.ok(clienteService.listarClientesActivos());
    }

    @Override
    public ResponseEntity<Cliente> buscarClientesPorDocumento(String documento) throws BadRequestException {
        return ResponseEntity.ok(clienteService.buscarClientesPorDocumento(documento));
    }

    @Override
    public ResponseEntity<Cliente> buscarClientePorId(Long id) {
        return ResponseEntity.ok(clienteService.buscarClientePorId(id));
    }

    @Override
    public ResponseEntity<MiRespuestaRS> crearCliente(ClienteRq clienteRq) {
        return ResponseEntity.status(HttpStatus.CREATED).body(clienteService.crearCliente(clienteRq));
    }

    @Override
    public ResponseEntity<MiRespuestaRS> actualizarCliente(Long id, ClienteRq clienteRq) {
        return ResponseEntity.ok(clienteService.actualizarCliente(id, clienteRq));
    }

    @Override
    public ResponseEntity<MiRespuestaRS> cambiarEstadoCliente(Long id, EstadoRq estadoRq) {
        Boolean activo = estadoRq == null ? null : estadoRq.getActivo();
        return ResponseEntity.ok(clienteService.cambiarEstadoCliente(id, activo));
    }
}
