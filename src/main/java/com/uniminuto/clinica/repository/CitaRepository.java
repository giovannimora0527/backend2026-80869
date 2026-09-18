package com.uniminuto.clinica.repository;

import com.uniminuto.clinica.entity.Cita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositorio para el acceso a datos de la entidad Cita.
 */
@Repository
public interface CitaRepository extends JpaRepository<Cita, Long> {

    /**
     * Consulta las citas dentro de un rango de fecha/hora ordenadas descendentemente.
     * @param fechaInicio Fecha y hora inicial del rango.
     * @param fechaFin Fecha y hora final del rango.
     * @return Lista de citas dentro del rango ordenadas de la mas reciente a la mas antigua.
     */
    List<Cita> findByFechaHoraBetweenOrderByFechaHoraDesc(LocalDateTime fechaInicio, LocalDateTime fechaFin);
}