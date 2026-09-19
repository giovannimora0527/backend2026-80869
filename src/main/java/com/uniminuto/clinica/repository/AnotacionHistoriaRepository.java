package com.uniminuto.clinica.repository;

import com.uniminuto.clinica.entity.AnotacionHistoria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositorio JPA para la entidad (AnotacionHistoria) solo para la persistencia (no se usan metodos JPA)
 */
public interface AnotacionHistoriaRepository extends JpaRepository<AnotacionHistoria, Long> {

    /**
     * se le dan dos parametros para buscar las anotaciones de historias médicas dentro de un rango de fechas, ordenadas de la más reciente a la más antigua.
     */
    List<AnotacionHistoria> findByFechaBetweenOrderByFechaDesc(LocalDateTime fechaInicio, LocalDateTime fechaFin);

    /**
     * lo mismo pero por id no por rango de fecha
     */
    List<AnotacionHistoria> findByHistoriaIdOrderByFechaDesc(Integer historiaId);
}