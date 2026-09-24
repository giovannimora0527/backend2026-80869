package com.veterinaria.repositorio;

import com.veterinaria.modelo.Medico;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicoRepository extends JpaRepository<Medico, Long> { 
    
}
