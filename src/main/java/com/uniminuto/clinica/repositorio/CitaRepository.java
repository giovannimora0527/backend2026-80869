package com.uniminuto.clinica.repositorio;

import com.uniminuto.clinica.models.Cita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositorio para la gestión de persistencia de la entidad Cita.
 * 
 * @author Andres Bernal
 */
@Repository
public interface CitaRepository extends JpaRepository<Cita, Long> {

    /**
     * Consulta las citas filtradas por rango de fecha y ordenadas descendentemente.
     */
    List<Cita> findByFechaHoraBetweenOrderByFechaHoraDesc(LocalDateTime inicio, LocalDateTime fin);

    /**
     * Valida si un médico ya posee una cita registrada en una fecha y hora específica dentro de estados válidos.
     */
    boolean existsByMedicoIdAndFechaHoraAndEstadoIn(Long medicoId, LocalDateTime fechaHora, List<String> estados);

    /**
     * Valida si un paciente ya posee una cita registrada en una fecha y hora específica dentro de estados válidos.
     */
    boolean existsByPacienteAndFechaHoraAndEstadoIn(String paciente, LocalDateTime fechaHora, List<String> estados);
}