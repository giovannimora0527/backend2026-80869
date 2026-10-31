package com.uniminuto.clinica.repository;

import com.uniminuto.clinica.entity.FormulaMedica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Acceso a datos de la tabla {@code formula_medica}.
 */
@Repository
public interface FormulaMedicaRepository
        extends JpaRepository<FormulaMedica, Long>, JpaSpecificationExecutor<FormulaMedica> {

    List<FormulaMedica> findAllByOrderByFechaCreacionRegistroDesc();

    List<FormulaMedica> findByCita_IdInOrderByFechaCreacionRegistroDesc(List<Long> citaIds);
}
