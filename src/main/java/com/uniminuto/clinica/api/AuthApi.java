package com.uniminuto.clinica.api;

import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.models.AuthRS;
import com.uniminuto.clinica.models.LoginRS;
import com.uniminuto.clinica.models.LoginRq;
import com.uniminuto.clinica.models.ReenviarOtpRq;
import com.uniminuto.clinica.models.ValidarOtpRq;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/auth")
public interface AuthApi {

    /** Paso 1: usuario + contraseña. Envia el OTP al correo. */
    @PostMapping(value = "/login",
            produces = {"application/json"},
            consumes = {"application/json"})
    ResponseEntity<AuthRS> login(@RequestBody LoginRq loginRq)
            throws BadRequestException;

    /** Paso 2: valida el OTP y devuelve el JWT, datos del usuario y menus. */
    @PostMapping(value = "/validar-otp",
            produces = {"application/json"},
            consumes = {"application/json"})
    ResponseEntity<LoginRS> validarOtp(@RequestBody ValidarOtpRq validarOtpRq)
            throws BadRequestException;

    /** Genera un OTP nuevo y lo envia al correo. */
    @PostMapping(value = "/reenviar-otp",
            produces = {"application/json"},
            consumes = {"application/json"})
    ResponseEntity<AuthRS> reenviarOtp(@RequestBody ReenviarOtpRq reenviarOtpRq)
            throws BadRequestException;
}
