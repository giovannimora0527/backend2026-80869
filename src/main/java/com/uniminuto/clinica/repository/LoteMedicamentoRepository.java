package com.uniminuto.clinica.repository;

import com.uniminuto.clinica.entity.LoteMedicamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoteMedicamentoRepository extends JpaRepository<LoteMedicamento, Long> {

    /**
     * Lotes de un medicamento ordenados por fecha de vencimiento ascendente
     * (los mas proximos a vencer primero). Util para la politica FEFO del
     * despacho.
     */
    List<LoteMedicamento> findByMedicamentoIdOrderByFechaVencimientoAsc(Long medicamentoId);

    List<LoteMedicamento> findByMedicamentoId(Long medicamentoId);
}
