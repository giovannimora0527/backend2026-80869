package com.uniminuto.clinica.repository;

import com.uniminuto.clinica.entity.DespachoDetalle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DespachoDetalleRepository extends JpaRepository<DespachoDetalle, Long> {

    List<DespachoDetalle> findByDespachoId(Long despachoId);
}
