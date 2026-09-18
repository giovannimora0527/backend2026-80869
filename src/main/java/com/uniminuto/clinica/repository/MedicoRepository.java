package com.uniminuto.clinica.repository;

import com.uniminuto.clinica.entity.Medico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio para el acceso a datos de la entidad Medico.
 */
@Repository
public interface MedicoRepository extends JpaRepository<Medico, Long> {
    // No necesitamos métodos personalizados porque findAll() ya trae las especializaciones
    // gracias a FetchType.EAGER en la entidad Medico
}