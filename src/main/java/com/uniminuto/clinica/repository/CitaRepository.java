package com.uniminuto.clinica.repository;

import com.uniminuto.clinica.entity.Cita;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositorio para el acceso a datos de la entidad Cita.
 */
public interface CitaRepository extends JpaRepository<Cita, Integer> {
    /**
     * Busca las citas cuya fecha_hora este entre fechaInicial y fechaFinal,
     * ordenadas de la mas reciente a la mas antigua.
     *
     * @param fechaInicial limite inferior del rango de fechas.
     * @param fechaFinal limite superior del rango de fechas.
     * @return lista de citas encontradas.
     */
    List<Cita> findByFechaHoraBetweenOrderByFechaHoraDesc(
            LocalDateTime fechaInicial, LocalDateTime fechaFinal);
}
