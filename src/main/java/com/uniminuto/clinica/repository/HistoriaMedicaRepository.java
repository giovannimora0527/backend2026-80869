package com.uniminuto.clinica.repository;

import com.uniminuto.clinica.entity.HistoriaMedica;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositorio JPA para la entidad
 * Provee las operaciones CRUD básicas heredadas de (JpaRepository)
 */
public interface HistoriaMedicaRepository extends JpaRepository<HistoriaMedica, Long> {

    /**
     * Busca las historias médicas cuya (fechaCreacion) esté dentro del
     * rango dado, ordenadas de la más reciente a la más antigua.
     */
    List<HistoriaMedica> findByFechaCreacionBetweenOrderByFechaCreacionDesc(LocalDateTime fechaInicial, LocalDateTime fechaFinal);
}