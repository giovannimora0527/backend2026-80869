package com.uniminuto.clinica.repository;

import com.uniminuto.clinica.entity.Cita;
import com.uniminuto.clinica.entity.Mascota;
import com.uniminuto.clinica.entity.Medico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio JPA para la entidad Cita.
 * Permite realizar operaciones CRUD y consultas personalizadas sobre citas veterinarias.
 *
 * @author Yulian Estephania Montealegre
 */
@Repository
public interface CitaRepository extends JpaRepository<Cita, Long> {

    /**
     * Busca citas dentro de un rango de fecha y hora.
     *
     * @param inicio Fecha inicial.
     * @param fin    Fecha final.
     * @return Lista de citas en ese rango.
     */
    List<Cita> findByFechaHoraBetween(LocalDateTime inicio, LocalDateTime fin);

    /**
     * Busca citas asignadas a un médico en un rango de fecha y hora.
     *
     * @param medico Entidad Medico.
     * @param inicio Fecha inicial.
     * @param fin    Fecha final.
     * @return Lista de citas del médico en el rango.
     */
    List<Cita> findByMedicoAndFechaHoraBetween(Medico medico, LocalDateTime inicio, LocalDateTime fin);

    /**
     * Busca citas asignadas a una mascota en un rango de fecha y hora.
     *
     * @param mascota Entidad Mascota.
     * @param inicio  Fecha inicial.
     * @param fin     Fecha final.
     * @return Lista de citas de la mascota en el rango.
     */
    List<Cita> findByMascotaAndFechaHoraBetween(Mascota mascota, LocalDateTime inicio, LocalDateTime fin);

    /**
     * Busca una cita específica por mascota y médico.
     *
     * @param mascota Entidad Mascota.
     * @param medico  Entidad Medico.
     * @return Optional con la cita si existe.
     */
    Optional<Cita> findByMascotaAndMedico(Mascota mascota, Medico medico);
}