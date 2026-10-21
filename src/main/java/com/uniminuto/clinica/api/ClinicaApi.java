package com.uniminuto.clinica.api;

import com.uniminuto.clinica.models.MiRespuestaRS;
import org.springframework.http.ResponseEntity;
<<<<<<< HEAD
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Contrato REST de verificación del servicio. Ruta base: {@code /clinica}.
 */
public interface ClinicaApi {

    /**
     * {@code GET /clinica/test}: comprueba que el backend responde.
     *
     * @return 200 con un mensaje de confirmación
     */
    @GetMapping("/test")
    ResponseEntity<String> testService();

    /**
     * {@code GET /clinica/test2}: segunda comprobación del servicio.
     *
     * @return 200 con un mensaje de confirmación
     */
    @GetMapping("/test2")
    ResponseEntity<String> testService2();

    /**
     * {@code GET /clinica/test3}: comprobación con respuesta estructurada.
     *
     * @return 200 con la respuesta estándar
     */
    @GetMapping("/test3")
    ResponseEntity<MiRespuestaRS> testService3();
=======
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.apache.coyote.BadRequestException;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/clinica")
public interface ClinicaApi {

    /**
     * Metodo test del servicio.
     *
     * @return Servicio funcionando correctamente.
     * @throws BadRequestException excepcion.
     */
    @GetMapping(value = "/test",
            produces = {"application/text"},
            consumes = {"application/json"})
    ResponseEntity<String> testService()
            throws BadRequestException;

    /**
     * Metodo test del servicio.
     *
     * @return Servicio funcionando correctamente.
     * @throws BadRequestException excepcion.
     */
    @GetMapping(value = "/test2",
            produces = {"application/text"},
            consumes = {"application/json"})
    ResponseEntity<String> testService2()
            throws BadRequestException;

    /**
     * Metodo test del servicio.
     *
     * @return Servicio funcionando correctamente.
     * @throws BadRequestException excepcion.
     */
    @GetMapping(value = "/test3",
            produces = {"application/json"},
            consumes = {"application/json"})
    ResponseEntity<MiRespuestaRS> testService3()
            throws BadRequestException;


>>>>>>> 3f64126562f972b8de9fb36bb740dd51a6fcca19
}
