package com.uniminuto.clinica.serviceimpl;

import com.uniminuto.clinica.entity.Cliente;
import com.uniminuto.clinica.entity.Mascota;
import com.uniminuto.clinica.entity.Raza;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.repository.ClienteRepository;
import com.uniminuto.clinica.repository.MascotaRepository;
import com.uniminuto.clinica.repository.RazaRepository;
import com.uniminuto.clinica.service.MascotaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MascotaServiceImpl implements MascotaService {

    @Autowired
    private MascotaRepository mascotaRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private RazaRepository razaRepository;

    @Override
    public List<Mascota> listarMascotas() {
        List<Mascota> mascotas = mascotaRepository.findAll();
        mascotas.sort((m1, m2) -> m1
                .getNombreMascota()
                .compareToIgnoreCase(m2.getNombreMascota()));
        return mascotas;
    }

    @Override
    public List<Mascota> listarMascotasOrdenado(boolean ascendente) {
        List<Mascota> mascotas;
        if (ascendente) {
            mascotas = mascotaRepository.findAllByOrderByNombreMascotaAsc();
        } else {
            mascotas = mascotaRepository.findAllByOrderByNombreMascotaDesc();
        }
        return mascotas;
    }

    @Override
    public Optional<Mascota> findByNombreMascota(String nombreMascota) {
        return Optional.ofNullable(mascotaRepository.findByNombreMascota(nombreMascota));
    }

    @Override
    public List<Mascota> buscarMascotasPorCliente(Long clienteId) throws BadRequestException {

        // Validar que cliente id != null
        if (clienteId == null) {
            throw new BadRequestException("El ID del cliente no puede ser nulo");
        }

        Optional<Cliente> optCliente = this.clienteRepository
                .findById(clienteId);

        if (optCliente.isEmpty()) {
            throw new BadRequestException("El cliente con ID " + clienteId + " no existe");
        }

        return this.mascotaRepository
                .findByClienteOrderByNombreMascotaAsc(optCliente.get());
    }

    @Override
    public List<Mascota> buscarMascotasPorRaza(Integer razaId) throws BadRequestException {

        // Pasoo 1. Validar que raza id != null
        if (razaId == null) {
            throw new BadRequestException("El ID de la raza no puede ser nulo");
        }

        // Paso 2. Validar que la raza existe
        Optional<Raza> optRaza = this.razaRepository
                .findById(razaId);

        // Valido que la raza existe, si no existe lanzo una excepción
        if (optRaza.isEmpty()) {
            throw new BadRequestException("La raza con ID " + razaId + " no existe");
        }

        // Paso 3. Buscar las mascotas por raza
        return this.mascotaRepository
                .findByRazaOrderByNombreMascotaAsc(optRaza.get());
    }
}
