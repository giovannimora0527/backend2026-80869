package com.uniminuto.clinica.service;

import com.uniminuto.clinica.entity.Mascota;
import java.util.Optional;

import java.util.List;

public interface MascotaService {

    List<Mascota> listarMascotas();

    List<Mascota> listarMascotasOrdenado(boolean ascendente);

    Optional<Mascota> findByNombreMascota(String nombreMascota);
}
