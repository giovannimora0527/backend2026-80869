package com.uniminuto.clinica.repository;

import com.uniminuto.clinica.entity.LogCargaInventario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LogCargaInventarioRepository extends JpaRepository<LogCargaInventario, Long> {

    /** Logs de carga masiva, del mas reciente al mas antiguo. */
    List<LogCargaInventario> findAllByOrderByFechaDesc();
}
