package com.uniminuto.clinica.repository;

import com.uniminuto.clinica.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuRepository extends JpaRepository<Menu, Integer> {

    /**
     * Menus activos que tiene asignado un perfil (tabla perfil_menu).
     */
    @Query("""
            SELECT m FROM Menu m
            WHERE m.activo = true
              AND m.menuId IN (
                SELECT pm.menuId FROM PerfilMenu pm WHERE pm.perfilId = :perfilId
              )
            ORDER BY m.menuId
            """)
    List<Menu> findByPerfilId(@Param("perfilId") Integer perfilId);
}
