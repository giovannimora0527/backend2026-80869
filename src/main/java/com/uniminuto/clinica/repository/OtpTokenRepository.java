package com.uniminuto.clinica.repository;

import com.uniminuto.clinica.entity.OtpToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OtpTokenRepository extends JpaRepository<OtpToken, Long> {

    Optional<OtpToken> findTopByUsuarioIdAndUsadoFalseOrderByFechaCreacionDesc(Long usuarioId);

    List<OtpToken> findByUsuarioIdAndUsadoFalse(Long usuarioId);
}
