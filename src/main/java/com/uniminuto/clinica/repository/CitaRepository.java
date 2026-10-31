package com.uniminuto.clinica.repository;

import com.uniminuto.clinica.entity.Cita;
import com.uniminuto.clinica.entity.Mascota;
import com.uniminuto.clinica.entity.Medico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Acceso a datos de la tabla {@code cita}.
 */
@Repository
public interface CitaRepository extends JpaRepository<Cita, Long>, JpaSpecificationExecutor<Cita> {

    List<Cita> findByFechaHoraBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin);

    Optional<Cita> findByMascotaAndMedico(Mascota mascota, Medico medico);

    List<Cita> findByMedicoAndFechaHoraBetween(Medico medico, LocalDateTime fechaInicio, LocalDateTime fechaFin);

    List<Cita> findByMascotaOrderByFechaHoraDesc(Mascota mascota);

    /**
     * Indica si un cliente tiene citas en alguno de los estados dados a partir de una fecha.
     * Se usa para impedir desactivar a un cliente que aún tiene citas vivas por delante.
     *
     * @param clienteId identificador del cliente.
     * @param estados   estados a considerar (por ejemplo las citas activas).
     * @param desde     solo se cuentan citas con fecha posterior a esta.
     * @return {@code true} si existe al menos una cita que cumple.
     */
    boolean existsByCliente_IdAndEstadoInAndFechaHoraAfter(Long clienteId,
                                                          Collection<String> estados,
                                                          LocalDateTime desde);

    /**
     * Cuenta las citas de un veterinario dentro de un rango, ignorando un estado
     * (las canceladas no ocupan agenda) y, opcionalmente, la propia cita que se edita.
     *
     * @param medicoId       veterinario.
     * @param desde          inicio del rango (inclusive).
     * @param hasta          fin del rango (inclusive).
     * @param estadoExcluido estado que no cuenta como ocupado (CANCELADA).
     * @param excluirId      id de la cita que se edita; usar {@code -1} al crear.
     * @return cantidad de citas que chocan con el rango.
     */
    @Query("SELECT COUNT(c) FROM Cita c WHERE c.medico.id = :medicoId "
            + "AND c.estado <> :estadoExcluido "
            + "AND c.fechaHora BETWEEN :desde AND :hasta AND c.id <> :excluirId")
    long contarPorMedicoEnRango(@Param("medicoId") Long medicoId,
                                @Param("desde") LocalDateTime desde,
                                @Param("hasta") LocalDateTime hasta,
                                @Param("estadoExcluido") String estadoExcluido,
                                @Param("excluirId") Long excluirId);

    /**
     * Igual que {@link #contarPorMedicoEnRango} pero para una mascota: una mascota
     * no puede tener dos citas simultáneas.
     *
     * @param mascotaId      mascota.
     * @param desde          inicio del rango (inclusive).
     * @param hasta          fin del rango (inclusive).
     * @param estadoExcluido estado que no cuenta como ocupado (CANCELADA).
     * @param excluirId      id de la cita que se edita; usar {@code -1} al crear.
     * @return cantidad de citas que chocan con el rango.
     */
    @Query("SELECT COUNT(c) FROM Cita c WHERE c.mascota.mascotaId = :mascotaId "
            + "AND c.estado <> :estadoExcluido "
            + "AND c.fechaHora BETWEEN :desde AND :hasta AND c.id <> :excluirId")
    long contarPorMascotaEnRango(@Param("mascotaId") Integer mascotaId,
                                 @Param("desde") LocalDateTime desde,
                                 @Param("hasta") LocalDateTime hasta,
                                 @Param("estadoExcluido") String estadoExcluido,
                                 @Param("excluirId") Long excluirId);
}
