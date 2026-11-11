package com.uniminuto.clinica.repository;

import com.uniminuto.clinica.entity.FormulaMedicamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FormulaMedicamentoRepository extends JpaRepository<FormulaMedicamento, Long> {

    List<FormulaMedicamento> findByFormulaId(Long formulaId);

    void deleteByFormulaId(Long formulaId);
}
