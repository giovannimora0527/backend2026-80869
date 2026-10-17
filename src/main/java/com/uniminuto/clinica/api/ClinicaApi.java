package com.uniminuto.clinica.api;

import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.models.MiRespuestaRS;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Interfaz que define los endpoints de prueba y estado del sistema de la clínica.
 *
 * @author Yulian Estefanía Montealegre
 */
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/clinica/v1")
public interface ClinicaApi {

    /**
     * Método test 1 del servicio.
     *
     * @return Mensaje de texto indicando que el servicio funciona.
     * @throws BadRequestException Excepción si ocurre un error en la solicitud.
     */
    @GetMapping(value = "/test", produces = "application/text")
    ResponseEntity<String> testService() throws BadRequestException;

    /**
     * Método test 2 del servicio.
     *
     * @return Mensaje de texto secundario.
     * @throws BadRequestException Excepción si ocurre un error en la solicitud.
     */
    @GetMapping(value = "/test2", produces = "application/text")
    ResponseEntity<String> testService2() throws BadRequestException;

    /**
     * Método test 3 del servicio que retorna un objeto de respuesta estandarizada.
     *
     * @return Objeto MiRespuestaRS con los detalles del estado.
     * @throws BadRequestException Excepción si ocurre un error en la solicitud.
     */
    @GetMapping(value = "/test3", produces = "application/json")
    ResponseEntity<MiRespuestaRS> testService3() throws BadRequestException;
}