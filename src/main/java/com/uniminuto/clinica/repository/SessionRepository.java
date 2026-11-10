package com.uniminuto.clinica.repository;

import com.uniminuto.clinica.entity.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {

    /**
     * Obtiene todas las sesiones asociadas a un usuario.
     * @param userId identificador del usuario.
     * @return lista de sesiones del usuario.
     */
    List<Session> findByUserId(Long userId);
}
