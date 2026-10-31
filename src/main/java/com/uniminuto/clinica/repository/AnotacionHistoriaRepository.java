package com.uniminuto.clinica.repository;

import com.uniminuto.clinica.entity.AnotacionHistoria;
import com.uniminuto.clinica.entity.HistoriaMedica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Acceso a datos de la tabla {@code anotacion_historia}.
 */
@Repository
public interface AnotacionHistoriaRepository
        extends JpaRepository<AnotacionHistoria, Long>, JpaSpecificationExecutor<AnotacionHistoria> {

    List<AnotacionHistoria> findByHistoria(HistoriaMedica historia);

    long countByHistoria(HistoriaMedica historia);
}
