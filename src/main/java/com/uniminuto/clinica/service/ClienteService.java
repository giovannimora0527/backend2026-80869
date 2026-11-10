package com.uniminuto.clinica.service;

import com.uniminuto.clinica.entity.Cliente;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.models.ClienteRq;
import com.uniminuto.clinica.models.MiRespuestaRS;

import java.util.List;

public interface ClienteService {

    List<Cliente> listarClientes() throws BadRequestException;

    Cliente buscarClientesPorDocumento(String documento) throws BadRequestException;

    MiRespuestaRS crearCliente(ClienteRq clienteRq) throws BadRequestException;

    MiRespuestaRS actualizarCliente(ClienteRq clienteRq) throws BadRequestException;
}
