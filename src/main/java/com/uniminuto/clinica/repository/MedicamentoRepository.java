package com.uniminuto.clinica.repository;

import com.uniminuto.clinica.entity.Medicamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio JPA para la entidad Medicamento.
 * Permite realizar operaciones CRUD y consultas sobre la tabla medicamento.
 *
 * @author Yulian Estephania Montealegre
 */
@Repository
public interface MedicamentoRepository extends JpaRepository<Medicamento, Long> {
}