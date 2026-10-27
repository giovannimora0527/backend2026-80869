package com.uniminuto.clinica.service;

import com.uniminuto.clinica.models.AuthRS;
import com.uniminuto.clinica.models.LoginRS;
import com.uniminuto.clinica.models.LoginRq;
import com.uniminuto.clinica.models.ReenviarOtpRq;
import com.uniminuto.clinica.models.ValidarOtpRq;

public interface AuthService {

    /** Paso 1: valida usuario y clave, genera y envia el OTP. */
    AuthRS login(LoginRq rq);

    /** Paso 2: valida el OTP y entrega el JWT con datos del usuario y menus. */
    LoginRS validarOtp(ValidarOtpRq rq);

    /** Genera y envia un OTP nuevo. */
    AuthRS reenviarOtp(ReenviarOtpRq rq);
}
