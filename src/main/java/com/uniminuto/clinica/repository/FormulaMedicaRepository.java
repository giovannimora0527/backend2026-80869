package com.uniminuto.clinica.repository;

import com.uniminuto.clinica.entity.FormulaMedica;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repositorio JPA para la entidad
 */
public interface FormulaMedicaRepository extends JpaRepository<FormulaMedica, Long> {

    /**
     * Obtiene todas las fórmulas médicas ordenadas por fecha de creación,
     * de la más reciente a la más antigua
     *
     * devuuelve la lista de fórmulas médicas ordenada descendentemente.
     */
    List<FormulaMedica> findAllByOrderByFechaCreacionRegistroDesc();
}