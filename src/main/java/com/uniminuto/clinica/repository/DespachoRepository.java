package com.uniminuto.clinica.repository;

import com.uniminuto.clinica.entity.Despacho;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DespachoRepository extends JpaRepository<Despacho, Long> {

    List<Despacho> findAllByOrderByFechaDesc();

    List<Despacho> findByEstadoOrderByFechaDesc(String estado);

    boolean existsByFormulaIdAndEstadoNot(Long formulaId, String estado);
}
