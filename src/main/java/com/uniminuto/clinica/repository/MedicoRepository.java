package com.uniminuto.clinica.repository;

import com.uniminuto.clinica.entity.Medico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio Spring Data JPA para la gestión de la entidad {@link Medico}.
 */
@Repository
public interface MedicoRepository extends JpaRepository<Medico, Long> {

    /**
     * Consulta optimizada mediante JOIN FETCH para obtener médicos junto con sus especializaciones en una única llamada,
     * evitando el problema de rendimiento N+1.
     *
     * @return Lista de médicos con sus especializaciones cargadas.
     */
    @Query("SELECT DISTINCT m FROM Medico m LEFT JOIN FETCH m.especializaciones")
    List<Medico> obtenerMedicosConEspecializaciones();
}