package com.uniminuto.clinica.apicontroller;

import com.uniminuto.clinica.api.AuthApi;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.models.AuthRS;
import com.uniminuto.clinica.models.LoginRS;
import com.uniminuto.clinica.models.LoginRq;
import com.uniminuto.clinica.models.ReenviarOtpRq;
import com.uniminuto.clinica.models.ValidarOtpRq;
import com.uniminuto.clinica.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthApiController implements AuthApi {

    @Autowired
    private AuthService authService;

    @Override
    public ResponseEntity<AuthRS> login(LoginRq loginRq) throws BadRequestException {
        return ResponseEntity.ok(authService.login(loginRq));
    }

    @Override
    public ResponseEntity<LoginRS> validarOtp(ValidarOtpRq validarOtpRq) throws BadRequestException {
        return ResponseEntity.ok(authService.validarOtp(validarOtpRq));
    }

    @Override
    public ResponseEntity<AuthRS> reenviarOtp(ReenviarOtpRq reenviarOtpRq) throws BadRequestException {
        return ResponseEntity.ok(authService.reenviarOtp(reenviarOtpRq));
    }
}
