
package com.uniminuto.clinica.repository;

import com.uniminuto.clinica.entity.AnotacionHistoria;
import com.uniminuto.clinica.entity.HistoriaMedica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnotacionHistoriaRepository
        extends JpaRepository<AnotacionHistoria, Long> {

    List<AnotacionHistoria> findByHistoriaOrderByFechaDesc(
            HistoriaMedica historia
    );
}
