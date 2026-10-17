package com.uniminuto.clinica.repository;

import com.uniminuto.clinica.entity.Raza;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio de Spring Data JPA para la entidad {@link Raza}.
 * Proporciona operaciones CRUD y consultas personalizadas para la gestión de razas en MySQL.
 *
 * @author Yulian Estephania Montealegre Rivas
 * @version 1.0
 * @see Raza
 * @see JpaRepository
 */
@Repository
public interface RazaRepository extends JpaRepository<Raza, Integer> {

    /**
     * Busca una raza en la base de datos por su nombre exacto.
     *
     * @param nombre El nombre de la raza a buscar.
     * @return Un {@link Optional} que contiene la raza si es encontrada, o vacío si no existe.
     */
    Optional<Raza> findByNombre(String nombre);

    /**
     * Busca una raza específica filtrando simultáneamente por su especie y su nombre.
     * Útil para validar duplicidades evitando cruces entre diferentes especies que compartan nombres.
     *
     * @param especie La especie animal (ej. "Canino", "Felino").
     * @param nombre  El nombre de la raza.
     * @return Un {@link Optional} que contiene la raza si coincide con ambos criterios.
     */
    Optional<Raza> findByEspecieAndNombre(String especie, String nombre);

}