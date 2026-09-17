package com.uniminuto.clinica.repository;

// JpaRepository es la interfaz que nos da Spring Data JPA
// Proporciona métodos CRUD automáticos (save, findById, delete, etc.)
import com.uniminuto.clinica.entity.FormulaMedica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Repositorio para el acceso a datos de formulas medicas.
 *
 * ¿Qué significa @Repository?
 * - Es una anotación de Spring que marca esta interfaz como componente
 * - Spring la detecta automáticamente y la inyecta donde se necesite
 * - Traduce las excepciones de JPA a excepciones de Spring
 */
@Repository
public interface FormulaMedicaRepository extends JpaRepository<FormulaMedica, Long> {

    /**
     * Consulta las formulas medicas ordenadas de la mas reciente a la mas antigua.
     *
     * ¿Cómo funciona este método sin implementación?
     * - Spring Data JPA "interpreta" el nombre del método:
     *   findAll = SELECT * FROM formula_medica
     *   ByOrderBy = añade ORDER BY
     *   FechaCreacionDesc = ORDER BY fecha_creacion DESC
     *
     * @return Lista de formulas medicas ordenadas por fecha de creacion descendente.
     */
    List<FormulaMedica> findAllByOrderByFechaCreacionDesc();
}