package com.veterinaria.repositorio;

import com.veterinaria.modelo.HistoriaMedica;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HistoriaMedicaRepository extends JpaRepository<HistoriaMedica, Long> { 
    
}