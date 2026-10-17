package com.uniminuto.clinica.service;

import com.uniminuto.clinica.entity.Cliente;
import com.uniminuto.clinica.models.MiRespuestaRS;

import java.util.List;

/**
 * Interfaz de servicio para la gestión de clientes.
 *
 * @author Yulian Estephania Montealegre
 */
public interface ClienteService {

    List<Cliente> listarClientes();

    Cliente buscarClientePorId(Long id);

    MiRespuestaRS guardarCliente(Cliente cliente);
}