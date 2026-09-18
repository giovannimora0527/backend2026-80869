package com.uniminuto.clinica.serviceimpl;

import com.uniminuto.clinica.entity.Cliente;
import com.uniminuto.clinica.entity.Mascota;
import com.uniminuto.clinica.entity.Raza;
import com.uniminuto.clinica.models.MascotaRq;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.repository.ClienteRepository;
import com.uniminuto.clinica.repository.MascotaRepository;
import com.uniminuto.clinica.repository.RazaRepository;
import com.uniminuto.clinica.service.MascotaService;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class MascotaServiceImpl implements MascotaService {

    @Autowired
    private MascotaRepository mascotaRepository;

    @Autowired
    private RazaRepository razaRepository;

    @Autowired
    private ClienteRepository clienteRepository;

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
    public List<Mascota> findByClienteId(Integer clienteId){
        if (clienteId <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing clientId in parameters");
        }
        return mascotaRepository.findByCliente_UsuarioId(clienteId);
    }

    @Override
    public List<Mascota> findByRazaId(Integer razaId){
        if (razaId <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing clientId in parameters");
        }
        return mascotaRepository.findByRaza_RazaId(razaId);
    }

    @Override
    public MiRespuestaRS crearMascota(MascotaRq mascotaRq) throws BadRequestException
    {
        if (!this.validarMascota(mascotaRq))
        {
            throw new BadRequestException("Faltan datos");
        }

        Optional<Raza> optRaza = this.razaRepository.findByRazaId(mascotaRq.getRazaId());
        if (optRaza.isEmpty()){
            throw new BadRequestException("La raza ingresada no es una raza valida");
        }

        Optional<Cliente> optCliente = this.clienteRepository.findByUsuarioId(mascotaRq.getClienteId());
        if (optCliente.isEmpty())
        {
            throw new BadRequestException("El cliente ingresado no es valido");
        }

        Mascota mascota = new Mascota();
        mascota.setNombreMascota(mascotaRq.getNombre());
        mascota.setEdad(mascotaRq.getEdad());
        mascota.setRaza(optRaza.get());
        mascota.setCliente(optCliente.get());
        mascota.setFechaRegistro(LocalDateTime.now());

        this.mascotaRepository.save(mascota);

        MiRespuestaRS response = new MiRespuestaRS();
        response.setStatus(200);
        response.setMessage("La mascota fue ingresada con exito");

        return response;
    }

    private boolean validarMascota(MascotaRq mascotaRq){
        if (mascotaRq == null) return false;
        if (mascotaRq.getNombre() == null || mascotaRq.getNombre() == "") return false;
        if (mascotaRq.getClienteId() == null || mascotaRq.getClienteId() <= 0) return false;
        if (mascotaRq.getEdad() <= 0 ) return false;
        if (mascotaRq.getRazaId() <= 0 ) return false;

        return true;
    }
}
