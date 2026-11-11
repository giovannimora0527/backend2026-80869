package com.uniminuto.clinica.repository;

import com.uniminuto.clinica.entity.LogTarifa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LogTarifaRepository extends JpaRepository<LogTarifa, Long> {

    /** Historial de cambios de tarifa de un servicio, del mas reciente al mas antiguo. */
    List<LogTarifa> findByServicioIdOrderByFechaDesc(Long servicioId);
}
