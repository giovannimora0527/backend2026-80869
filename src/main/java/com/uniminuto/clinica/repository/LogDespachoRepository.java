package com.uniminuto.clinica.repository;

import com.uniminuto.clinica.entity.LogDespacho;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LogDespachoRepository extends JpaRepository<LogDespacho, Long> {

    List<LogDespacho> findByDespachoIdOrderByFechaAsc(Long despachoId);
}
