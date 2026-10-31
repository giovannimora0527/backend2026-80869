package com.uniminuto.clinica.repository;

import com.uniminuto.clinica.entity.HistoriaMedica;
import com.uniminuto.clinica.entity.Mascota;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Acceso a datos de la tabla {@code historia_medica}.
 */
@Repository
public interface HistoriaMedicaRepository
        extends JpaRepository<HistoriaMedica, Long>, JpaSpecificationExecutor<HistoriaMedica> {

    List<HistoriaMedica> findByMascotaOrderByFechaConsultaDesc(Mascota mascota);

    Optional<HistoriaMedica> findByCita_Id(Long citaId);

    boolean existsByCita_Id(Long citaId);

    boolean existsByCita_IdAndIdNot(Long citaId, Long id);
}
