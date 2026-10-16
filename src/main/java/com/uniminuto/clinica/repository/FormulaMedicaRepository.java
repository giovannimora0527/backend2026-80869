package com.uniminuto.clinica.repository;

import com.uniminuto.clinica.entity.FormulaMedica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio para acceder a la tabla formula_medica.
 * @author Yulian Montealegre
 */
@Repository
public interface FormulaMedicaRepository extends JpaRepository<FormulaMedica, Long> {

    /**
     * Obtiene todas las fórmulas médicas ordenadas por fecha de creación descendente.
     */
    List<FormulaMedica> findAllByOrderByFechaCreacionRegistroDesc();
}