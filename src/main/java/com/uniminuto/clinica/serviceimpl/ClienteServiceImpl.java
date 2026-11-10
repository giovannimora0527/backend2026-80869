package com.uniminuto.clinica.serviceimpl;

import com.uniminuto.clinica.entity.Cliente;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.models.ClienteRq;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.repository.ClienteRepository;
import com.uniminuto.clinica.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClienteServiceImpl implements ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;


    @Override
    public List<Cliente> listarClientes() throws BadRequestException {
        return clienteRepository.findAllByOrderByNombresAsc();
    }

    @Override
    public Cliente buscarClientesPorDocumento(String documento) throws BadRequestException {
        Optional<Cliente> clienteOptional = clienteRepository.findByNumeroDocumento(documento);
        if (clienteOptional.isPresent()) {
            return clienteOptional.get();
        } else {
            throw new BadRequestException("Cliente no encontrado con el documento: " + documento);
        }
    }

    @Override
    public MiRespuestaRS crearCliente(ClienteRq rq) throws BadRequestException {
        this.validar(rq);
        if (clienteRepository.existsByNumeroDocumento(rq.getNumeroDocumento().trim())) {
            throw new BadRequestException("Ya existe un cliente con ese numero de documento.");
        }
        Cliente cliente = new Cliente();
        this.aplicarDatos(cliente, rq);
        clienteRepository.save(cliente);

        MiRespuestaRS rta = new MiRespuestaRS();
        rta.setStatus(200);
        rta.setMessage("Cliente creado correctamente.");
        return rta;
    }

    @Override
    public MiRespuestaRS actualizarCliente(ClienteRq rq) throws BadRequestException {
        this.validar(rq);
        if (rq.getId() == null) {
            throw new BadRequestException("El identificador del cliente es obligatorio.");
        }
        Optional<Cliente> opt = clienteRepository.findById(rq.getId());
        if (opt.isEmpty()) {
            throw new BadRequestException("El cliente no existe.");
        }
        if (clienteRepository.existsByNumeroDocumentoAndIdNot(rq.getNumeroDocumento().trim(), rq.getId())) {
            throw new BadRequestException("Ya existe otro cliente con ese numero de documento.");
        }
        Cliente cliente = opt.get();
        this.aplicarDatos(cliente, rq);
        clienteRepository.save(cliente);

        MiRespuestaRS rta = new MiRespuestaRS();
        rta.setStatus(200);
        rta.setMessage("Cliente actualizado correctamente.");
        return rta;
    }

    private void aplicarDatos(Cliente cliente, ClienteRq rq) {
        cliente.setTipoDocumento(rq.getTipoDocumento());
        cliente.setNumeroDocumento(rq.getNumeroDocumento().trim());
        cliente.setNombres(rq.getNombres().trim());
        cliente.setApellidos(rq.getApellidos().trim());
        cliente.setFechaNacimiento(rq.getFechaNacimiento());
        cliente.setGenero(rq.getGenero());
        cliente.setTelefono(rq.getTelefono());
        cliente.setDireccion(rq.getDireccion());
        cliente.setActivo(rq.getActivo() != null ? rq.getActivo() : Boolean.TRUE);
    }

    private void validar(ClienteRq rq) throws BadRequestException {
        if (rq == null) {
            throw new BadRequestException("Los datos del cliente son obligatorios.");
        }
        if (rq.getTipoDocumento() == null || rq.getTipoDocumento().isBlank()) {
            throw new BadRequestException("El tipo de documento es obligatorio.");
        }
        if (rq.getNumeroDocumento() == null || rq.getNumeroDocumento().isBlank()) {
            throw new BadRequestException("El numero de documento es obligatorio.");
        }
        if (rq.getNombres() == null || rq.getNombres().isBlank()) {
            throw new BadRequestException("Los nombres son obligatorios.");
        }
        if (rq.getApellidos() == null || rq.getApellidos().isBlank()) {
            throw new BadRequestException("Los apellidos son obligatorios.");
        }
        if (rq.getFechaNacimiento() == null) {
            throw new BadRequestException("La fecha de nacimiento es obligatoria.");
        }
    }
}
