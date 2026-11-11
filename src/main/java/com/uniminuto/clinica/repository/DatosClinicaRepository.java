package com.uniminuto.clinica.repository;

import com.uniminuto.clinica.entity.DatosClinica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DatosClinicaRepository extends JpaRepository<DatosClinica, Long> {
}
