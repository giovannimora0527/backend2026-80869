package com.veterinaria.repositorio;

import com.veterinaria.modelo.FormulaMedica;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FormulaMedicaRepository extends JpaRepository<FormulaMedica, Long>{
    List<FormulaMedica> findAllByOrderByFechaCreacionDesc();
    
}