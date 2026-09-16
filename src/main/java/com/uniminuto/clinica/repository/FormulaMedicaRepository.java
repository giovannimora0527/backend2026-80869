package com.uniminuto.clinica.repository;

import com.uniminuto.clinica.entity.FormulaMedica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio para el acceso a datos de formulas medicas.
 */
@Repository
public interface FormulaMedicaRepository extends JpaRepository<FormulaMedica, Long> {

    /**
     * Consulta las formulas medicas ordenadas de la mas reciente a la mas antigua.
     *
     * @return Lista de formulas medicas ordenadas por fecha de creacion descendente.
     */
    List<FormulaMedica> findAllByOrderByFechaCreacionDesc();
}