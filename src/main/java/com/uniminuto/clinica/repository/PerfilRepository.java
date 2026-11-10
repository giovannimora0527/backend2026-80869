package com.uniminuto.clinica.repository;

import com.uniminuto.clinica.entity.Perfil;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PerfilRepository extends JpaRepository<Perfil, Integer> {

    List<Perfil> findAllByOrderByNombreAsc();

    boolean existsByNombreIgnoreCase(String nombre);

    boolean existsByNombreIgnoreCaseAndPerfilIdNot(String nombre, Integer perfilId);
}
