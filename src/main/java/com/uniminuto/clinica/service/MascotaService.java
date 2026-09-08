package com.uniminuto.clinica.service;

import com.uniminuto.clinica.entity.Mascota;
import com.uniminuto.clinica.models.MascotaRq;
import com.uniminuto.clinica.models.MiRespuestaRS;
import org.apache.coyote.BadRequestException;

import java.util.Optional;

import java.util.List;

public interface MascotaService {

    List<Mascota> listarMascotas();

    List<Mascota> listarMascotasOrdenado(boolean ascendente);

    Optional<Mascota> findByNombreMascota(String nombreMascota);

    List<Mascota> findByClienteId(Integer clienteId);

    List<Mascota> findByRazaId(Integer razaId);

    MiRespuestaRS crearMascota(MascotaRq mascotaRq) throws BadRequestException;
}
