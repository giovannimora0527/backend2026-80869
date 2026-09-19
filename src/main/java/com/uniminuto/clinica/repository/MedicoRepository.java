package com.uniminuto.clinica.repository;

import com.uniminuto.clinica.entity.Medico;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio JPA para la entidad medico
 * Provee las operaciones CRUD básicas heredadas de (JPARepository)
 */
public interface MedicoRepository extends JpaRepository<Medico, Long> {
}