package com.uniminuto.clinica.serviceimpl;

import com.uniminuto.clinica.entity.Cliente;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.exception.ResourceNotFoundException;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.repository.ClienteRepository;
import com.uniminuto.clinica.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementación de la lógica de negocio para la gestión de Clientes.
 *
 * @author Yulian Estephania Montealegre
 */
@Service
public class ClienteServiceImpl implements ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    /**
     * Obtiene el listado completo de clientes registrados.
     */
    @Override
    public List<Cliente> listarClientes() {
        return clienteRepository.findAll();
    }

    /**
     * Busca un cliente por su identificador único.
     */
    @Override
    public Cliente buscarClientePorId(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("El cliente con ID " + id + " no fue encontrado."));
    }

    /**
     * Registra un nuevo cliente validando sus datos básicos.
     */
    @Override
    public MiRespuestaRS guardarCliente(Cliente cliente) {
        if (cliente == null || cliente.getNumeroDocumento() == null || cliente.getNombres() == null) {
            throw new BadRequestException("Los datos básicos del cliente son obligatorios.");
        }

        clienteRepository.save(cliente);

        MiRespuestaRS rta = new MiRespuestaRS();
        rta.setStatus(200);
        rta.setMessage("Cliente " + cliente.getNombres() + " registrado exitosamente.");
        return rta;
    }
}