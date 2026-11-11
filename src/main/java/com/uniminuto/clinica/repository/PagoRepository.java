package com.uniminuto.clinica.repository;

import com.uniminuto.clinica.entity.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Long> {

    List<Pago> findByFacturaIdOrderByFechaAsc(Long facturaId);
}
