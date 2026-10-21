package com.uniminuto.clinica.serviceimpl;

import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.service.ClinicaService;
<<<<<<< HEAD
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

/**
 * Implementación del servicio de verificación del backend.
 */
@Service
public class ClinicaServiceImpl implements ClinicaService {

    /**
     * {@inheritDoc}
     */
    @Override
    public String testService() {
        return "Servicio funcionando correctamente";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String testService2() {
        return "Test 2 ejecutado correctamente";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MiRespuestaRS testService3() {
        return MiRespuestaRS.of(HttpStatus.OK.value(), "Test 3 ejecutado correctamente");
=======
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;

@Service
public class ClinicaServiceImpl implements ClinicaService {

    @Override
    public String testService2() throws BadRequestException {
        return "Servicio ok";
    }

    @Override
    public MiRespuestaRS testService3() throws BadRequestException {
        MiRespuestaRS rta = new MiRespuestaRS();
        rta.setMessage("probando servicio con objeto personalizado");
        rta.setStatus(201);
        return rta;
>>>>>>> 3f64126562f972b8de9fb36bb740dd51a6fcca19
    }
}
