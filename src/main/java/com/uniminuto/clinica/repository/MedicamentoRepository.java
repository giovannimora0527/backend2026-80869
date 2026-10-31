package com.uniminuto.clinica.repository;

import com.uniminuto.clinica.entity.Medicamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Acceso a datos de la tabla {@code medicamento}.
 */
@Repository
public interface MedicamentoRepository extends JpaRepository<Medicamento, Long> {

    /**
     * Indica si ya existe un medicamento con ese nombre, sin importar mayúsculas.
     *
     * @param nombre nombre a verificar.
     * @return {@code true} si ya existe.
     */
    boolean existsByNombreIgnoreCase(String nombre);

    /**
     * Indica si otro medicamento (distinto al indicado) usa ese nombre.
     *
     * @param nombre nombre a verificar.
     * @param id     identificador del medicamento que se está editando.
     * @return {@code true} si otro medicamento ya lo usa.
     */
    boolean existsByNombreIgnoreCaseAndIdNot(String nombre, Long id);

    /**
     * Lista los medicamentos según su estado, ordenados por nombre.
     *
     * @param activo {@code true} para activos, {@code false} para inactivos.
     * @return lista ordenada.
     */
    List<Medicamento> findByActivoOrderByNombreAsc(Boolean activo);
}
