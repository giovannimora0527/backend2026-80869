package com.uniminuto.clinica.serviceimpl;

import com.uniminuto.clinica.entity.Mascota;
import com.uniminuto.clinica.repository.MascotaRepository;
import com.uniminuto.clinica.service.MascotaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class MascotaServiceImpl implements MascotaService {

    @Autowired
    private MascotaRepository mascotaRepository;

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
}
