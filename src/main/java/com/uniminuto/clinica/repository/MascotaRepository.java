package com.uniminuto.clinica.repository;

import com.uniminuto.clinica.entity.Mascota;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MascotaRepository extends JpaRepository<Mascota, Integer> {

    List<Mascota> findAllByOrderByNombreMascotaAsc();

    List<Mascota> findAllByOrderByNombreMascotaDesc();

    Mascota findByNombreMascota(String nombreMascota);

    List<Mascota> findByCliente_UsuarioId(Integer clienteId);

    List<Mascota> findByRaza_RazaId(Integer razaId);
}
