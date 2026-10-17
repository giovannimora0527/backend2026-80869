package com.uniminuto.clinica.serviceimpl;

import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.service.ClinicaService;
import org.springframework.stereotype.Service;

/**
 * Implementación de la lógica de negocio para los servicios de prueba y diagnóstico de la clínica.
 *
 * @author Yulian Estefanía Montealegre
 */
@Service
public class ClinicaServiceImpl implements ClinicaService {

    /**
     * Servicio de prueba 1.
     *
     * @return Mensaje de texto de comprobación.
     * @throws BadRequestException Si ocurre algún error en la solicitud.
     */
    @Override
    public String testService() throws BadRequestException {
        return "Servicio ok 1";
    }

    /**
     * Servicio de prueba 2.
     *
     * @return Mensaje de texto de comprobación.
     * @throws BadRequestException Si ocurre algún error en la solicitud.
     */
    @Override
    public String testService2() throws BadRequestException {
        return "Servicio ok";
    }

    /**
     * Servicio de prueba 3 que retorna una respuesta estandarizada.
     *
     * @return Objeto MiRespuestaRS con el estado de éxito.
     * @throws BadRequestException Si ocurre algún error en la solicitud.
     */
    @Override
    public MiRespuestaRS testService3() throws BadRequestException {
        MiRespuestaRS respuesta = new MiRespuestaRS();
        respuesta.setStatus(200);
        respuesta.setMessage("Servicio ok desde clase y objeto");
        return respuesta;
    }
}