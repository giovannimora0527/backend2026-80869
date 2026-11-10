package com.uniminuto.clinica.repository;

import com.uniminuto.clinica.entity.HorarioMedico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HorarioMedicoRepository extends JpaRepository<HorarioMedico, Long> {

    List<HorarioMedico> findByMedicoIdAndActivoTrue(Long medicoId);

    List<HorarioMedico> findByMedicoId(Long medicoId);
}
