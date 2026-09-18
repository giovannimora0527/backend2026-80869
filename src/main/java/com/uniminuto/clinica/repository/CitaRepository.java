package com.uniminuto.clinica.repository;

import com.uniminuto.clinica.entity.Cita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositorio para el acceso a datos de la entidad Cita.
 *
 * ¿Qué hace @Repository?
 * - Le dice a Spring que esta interfaz es un componente de acceso a datos
 * - Spring la detecta automáticamente y la inyecta en los servicios
 * - Traduce las excepciones de JPA a excepciones de Spring automáticamente
 */
@Repository
public interface CitaRepository extends JpaRepository<Cita, Long> {

    /**
     * Consulta las citas dentro de un rango de fecha/hora ordenadas descendentemente.
     *
     * ¿Cómo funciona este método sin implementación?
     * - Spring Data JPA "interpreta" el nombre del método y genera el SQL automáticamente:
     *   findBy = SELECT * FROM cita WHERE
     *   FechaHoraBetween = fecha_hora BETWEEN ? AND ?
     *   OrderByFechaHoraDesc = ORDER BY fecha_hora DESC
     *
     * Ejemplo de SQL generado:
     * SELECT * FROM cita WHERE fecha_hora BETWEEN '2026-01-01' AND '2026-12-31' ORDER BY fecha_hora DESC
     *
     * @param fechaInicio Fecha y hora inicial del rango.
     * @param fechaFin    Fecha y hora final del rango.
     * @return Lista de citas dentro del rango ordenadas de la mas reciente a la mas antigua.
     */
    List<Cita> findByFechaHoraBetweenOrderByFechaHoraDesc(
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin
    );
}