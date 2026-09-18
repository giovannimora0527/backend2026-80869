package com.uniminuto.clinica.repository;

import com.uniminuto.clinica.entity.AnotacionHistoria;
import com.uniminuto.clinica.entity.HistoriaMedica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AnotacionHistoriaRepository extends JpaRepository<AnotacionHistoria, Integer> {
    List<AnotacionHistoria> findByHistoriaMedicaOrderByFechaDesc(HistoriaMedica historiaMedica);
    List<AnotacionHistoria> findByHistoriaMedicaAndFechaBetweenOrderByFechaDesc(HistoriaMedica historiaMedica, LocalDateTime start, LocalDateTime end);
    List<AnotacionHistoria> findByFechaBetweenOrderByFechaDesc(LocalDateTime start, LocalDateTime end);
    List<AnotacionHistoria> findAllByOrderByFechaDesc();
}
