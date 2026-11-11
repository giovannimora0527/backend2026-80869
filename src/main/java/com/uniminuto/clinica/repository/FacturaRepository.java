package com.uniminuto.clinica.repository;

import com.uniminuto.clinica.entity.Factura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FacturaRepository extends JpaRepository<Factura, Long> {

    List<Factura> findAllByOrderByFechaDesc();

    List<Factura> findByClienteIdOrderByFechaDesc(Long clienteId);

    boolean existsByCitaIdAndEstadoPagoNot(Long citaId, String estadoPago);
}
