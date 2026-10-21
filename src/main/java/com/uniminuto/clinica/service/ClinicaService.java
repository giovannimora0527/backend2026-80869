package com.uniminuto.clinica.service;

import com.uniminuto.clinica.models.MiRespuestaRS;
<<<<<<< HEAD

/**
 * Servicio de verificación (health check) del backend. Las operaciones clínicas viven en
 * sus propios servicios (citas, historias, anotaciones, fórmulas).
 */
public interface ClinicaService {

    /**
     * Prueba básica de que el servicio responde.
     *
     * @return mensaje de confirmación
     */
    String testService();

    /**
     * Segunda prueba del servicio.
     *
     * @return mensaje de confirmación
     */
    String testService2();

    /**
     * Prueba que devuelve una respuesta estructurada.
     *
     * @return respuesta con estado 200 y mensaje de confirmación
     */
    MiRespuestaRS testService3();
=======
import org.apache.coyote.BadRequestException;

public interface ClinicaService {

    String testService2() throws BadRequestException;


    MiRespuestaRS testService3() throws BadRequestException;
>>>>>>> 3f64126562f972b8de9fb36bb740dd51a6fcca19
}
