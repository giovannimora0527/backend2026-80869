package com.uniminuto.clinica.serviceimpl;

import com.uniminuto.clinica.service.ClinicaService;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;

@Service
public class ClinicaServiceImpl implements ClinicaService {

    @Override
    public String testService2() throws BadRequestException {
        return "Servicio ok";
    }
}
