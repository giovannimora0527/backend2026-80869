package com.uniminuto.clinica.service;

import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.models.MiRespuestaRS;

/**
 * Interfaz que define los servicios de diagnóstico y pruebas del sistema de la clínica.
 *
 * @author Yulian Estefanía Montealegre
 */
public interface ClinicaService {

    /**
     * Servicio de prueba 1.
     *
     * @return Mensaje de texto de comprobación.
     * @throws BadRequestException Si ocurre un error en la solicitud.
     */
    String testService() throws BadRequestException;

    /**
     * Servicio de prueba 2.
     *
     * @return Mensaje de texto secundario.
     * @throws BadRequestException Si ocurre un error en la solicitud.
     */
    String testService2() throws BadRequestException;

    /**
     * Servicio de prueba 3 que retorna una respuesta estandarizada.
     *
     * @return Objeto MiRespuestaRS con el estado de éxito.
     * @throws BadRequestException Si ocurre un error en la solicitud.
     */
    MiRespuestaRS testService3() throws BadRequestException;
}