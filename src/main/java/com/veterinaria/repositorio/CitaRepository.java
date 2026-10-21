package com.veterinaria.repositorio;

import com.veterinaria.modelo.Cita;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface CitaRepository extends JpaRepository<Cita, Long>{
    List<Cita> findByFechaHoraBetweenOrderByFechaHoraDesc(LocalDateTime inicio, LocalDateTime fin);
    
}