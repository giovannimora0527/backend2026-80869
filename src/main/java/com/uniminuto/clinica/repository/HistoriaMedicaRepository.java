package com.uniminuto.clinica.repository;

import com.uniminuto.clinica.entity.HistoriaMedica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Repositorio JPA para la entidad HistoriaMedica.
 *
 * @author Yulian Estephania
 * @version 1.0
 */
@Repository
public interface HistoriaMedicaRepository extends JpaRepository<HistoriaMedica, Long> {

    List<HistoriaMedica> findAllByOrderByFechaHoraDesc();

    boolean existsByCitaId(Long citaId);
}