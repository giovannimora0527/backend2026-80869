package com.uniminuto.clinica.service;

import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.models.MascotaRq;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.models.RazaRq;

public interface RazaService {

    MiRespuestaRS crearRazaNueva(RazaRq razaRq) throws BadRequestException;
}
