package com.uniminuto.clinica.service;

import com.uniminuto.clinica.entity.Cliente;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.models.ClienteRq;
import com.uniminuto.clinica.models.MiRespuestaRS;

import java.util.List;

/**
 * Reglas de negocio de la gestión de clientes.
 */
public interface ClienteService {

    /**
     * Lista todos los clientes (activos e inactivos) ordenados por nombre.
     *
     * @return lista de clientes.
     */
    List<Cliente> listarClientes() throws BadRequestException;

    /**
     * Lista solo los clientes activos; se usa en los combos de otros módulos.
     *
     * @return lista de clientes activos.
     */
    List<Cliente> listarClientesActivos();

    /**
     * Busca un cliente por su documento.
     *
     * @param documento número de documento.
     * @return el cliente encontrado.
     */
    Cliente buscarClientesPorDocumento(String documento) throws BadRequestException;

    /**
     * Busca un cliente por su identificador.
     *
     * @param id identificador del cliente.
     * @return el cliente encontrado.
     */
    Cliente buscarClientePorId(Long id);

    /**
     * Crea un cliente nuevo (queda activo).
     *
     * @param rq datos del cliente.
     * @return mensaje de confirmación.
     */
    MiRespuestaRS crearCliente(ClienteRq rq);

    /**
     * Actualiza los datos de un cliente existente.
     *
     * @param id identificador del cliente.
     * @param rq nuevos datos.
     * @return mensaje de confirmación.
     */
    MiRespuestaRS actualizarCliente(Long id, ClienteRq rq);

    /**
     * Activa o desactiva un cliente (borrado lógico).
     *
     * @param id     identificador del cliente.
     * @param activo estado deseado.
     * @return mensaje de confirmación.
     */
    MiRespuestaRS cambiarEstadoCliente(Long id, Boolean activo);
}
