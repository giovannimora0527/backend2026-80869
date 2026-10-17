package com.uniminuto.clinica.apicontroller;

import com.uniminuto.clinica.api.ClinicaApi;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.service.ClinicaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST que implementa los endpoints de diagnóstico definidos en ClinicaApi.
 *
 * @author Yulian Estefanía Montealegre
 */
@RestController
public class ClinicaApiController implements ClinicaApi {

    @Autowired
    private ClinicaService clinicaService;

    /**
     * Endpoint de prueba 1.
     */
    @Override
    public ResponseEntity<String> testService() throws BadRequestException {
        return ResponseEntity.ok("Servicio funcionando correctamente");
    }

    /**
     * Endpoint de prueba 2.
     */
    @Override
    public ResponseEntity<String> testService2() throws BadRequestException {
        return ResponseEntity.ok(clinicaService.testService2());
    }

    /**
     * Endpoint de prueba 3.
     */
    @Override
    public ResponseEntity<MiRespuestaRS> testService3() throws BadRequestException {
        return ResponseEntity.ok(clinicaService.testService3());
    }
}