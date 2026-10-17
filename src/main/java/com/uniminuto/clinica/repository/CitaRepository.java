
package com.uniminuto.clinica.repository;

import com.uniminuto.clinica.entity.Cita;
import com.uniminuto.clinica.entity.Mascota;
import com.uniminuto.clinica.entity.Medico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CitaRepository extends JpaRepository<Cita, Long> {

    List<Cita> findByFechaHoraBetween(
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin
    );

    List<Cita> findByMascotaAndFechaHoraBetween(
            Mascota mascota,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin
    );

    List<Cita> findByMedicoAndFechaHoraBetween(
            Medico medico,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin
    );

    List<Cita> findByMascotaAndFechaHoraBetweenAndIdNot(
            Mascota mascota,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin,
            Long id
    );

    List<Cita> findByMedicoAndFechaHoraBetweenAndIdNot(
            Medico medico,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin,
            Long id
    );
}
