package com.uniminuto.clinica.repository;

import com.uniminuto.clinica.entity.Cita;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Se conecta con la base de datos mediante Spring data JPA (sin necesidad de escribir consultas sql)
 */
public interface CitaRepository extends JpaRepository<Cita, Long> {

    /**
     * Busca las citas cuya esté dentro del rango dado,
     * ordenadas de la más reciente a la más antigua.
     */
    List<Cita> findByFechaHoraBetweenOrderByFechaHoraDesc(LocalDateTime fechaInicio, LocalDateTime fechaFin);
}
