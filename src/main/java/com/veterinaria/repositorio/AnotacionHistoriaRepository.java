package com.veterinaria.repositorio;

import com.veterinaria.modelo.AnotacionHistoria;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface AnotacionHistoriaRepository extends JpaRepository<AnotacionHistoria, Long> {
    List<AnotacionHistoria> findByFechaCreacionBetweenOrderByFechaCreacionDesc(LocalDateTime inicio, LocalDateTime fin);
    
}