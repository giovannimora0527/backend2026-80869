package com.uniminuto.clinica.repository;

import com.uniminuto.clinica.entity.AnotacionHistoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositorio Spring Data JPA para la entidad {@link AnotacionHistoria}.
 */
@Repository
public interface AnotacionHistoriaRepository extends JpaRepository<AnotacionHistoria, Long> {

    /**
     * Busca anotaciones en un rango de fechas ordenadas descendentemente.
     *
     * @param fechaInicio Fecha inicial de búsqueda.
     * @param fechaFin Fecha final de búsqueda.
     * @return Lista de anotaciones dentro del rango.
     */
    List<AnotacionHistoria> findByFechaCreacionBetweenOrderByFechaCreacionDesc(LocalDateTime fechaInicio, LocalDateTime fechaFin);

    /**
     * Recupera todas las anotaciones ordenadas de la más reciente a la más antigua.
     *
     * @return Lista completa ordenada descendentemente.
     */
    List<AnotacionHistoria> findAllByOrderByFechaCreacionDesc();
}