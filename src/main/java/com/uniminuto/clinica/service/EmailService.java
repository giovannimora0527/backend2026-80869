package com.uniminuto.clinica.service;

public interface EmailService {

    /**
     * Envia el codigo OTP al correo del usuario.
     */
    void enviarOtp(String destino, String username, String codigo, int minutosVigencia);
}
