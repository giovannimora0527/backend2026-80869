package com.uniminuto.clinica.repository;

import com.uniminuto.clinica.entity.AnotacionHistoria;
import com.uniminuto.clinica.entity.HistoriaMedica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio JPA para la entidad AnotacionHistoria.
 * Permite realizar operaciones de persistencia sobre las notas de la historia clínica.
 *
 * @author Yulian Estephania Montealegre
 */
@Repository
public interface AnotacionHistoriaRepository extends JpaRepository<AnotacionHistoria, Long> {

    /**
     * Busca todas las anotaciones asociadas a una historia clínica específica,
     * ordenadas de la más reciente a la más antigua.
     *
     * @param historia Objeto HistoriaMedica.
     * @return Lista de anotaciones de historia.
     */
    List<AnotacionHistoria> findByHistoriaOrderByFechaDesc(HistoriaMedica historia);
}