package com.uniminuto.clinica.repository;

import com.uniminuto.clinica.entity.MovimientoInventario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovimientoInventarioRepository extends JpaRepository<MovimientoInventario, Long> {

    /** Historial de movimientos de un medicamento, del mas reciente al mas antiguo. */
    List<MovimientoInventario> findByMedicamentoIdOrderByFechaDesc(Long medicamentoId);
}
