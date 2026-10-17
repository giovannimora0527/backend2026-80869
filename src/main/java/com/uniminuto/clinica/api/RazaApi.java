package com.uniminuto.clinica.api;

import com.uniminuto.clinica.entity.Raza;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.models.RazaRq;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * Interfaz que define los endpoints (contrato API REST) para la gestión de razas
 * en el backend de la clínica veterinaria.
 *
 * @author Yulian Estefanía Montealegre Rivas
 * @version 1.0
 */
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/raza")
public interface RazaApi {

    /**
     * Endpoint para registrar y almacenar una nueva raza en el sistema.
     *
     * @param razaRq Objeto de transferencia de datos con la información de la raza (nombre y especie).
     * @return Una respuesta estándar {@link MiRespuestaRS} indicando el éxito de la operación.
     * @throws BadRequestException Si los datos de entrada son inválidos o la raza ya existe.
     */
    @PostMapping(value = "/guardar",
            produces = {"application/json"},
            consumes = {"application/json"})
    ResponseEntity<MiRespuestaRS> guardarRaza(
            @RequestBody RazaRq razaRq)
            throws BadRequestException;

    /**
     * Endpoint para obtener el listado completo de las razas registradas en el sistema.
     *
     * @return Una lista con todas las entidades {@link Raza} encontradas.
     * @throws BadRequestException Si ocurre algún error en la consulta.
     */
    @GetMapping(value = "/listar",
            produces = {"application/json"})
    ResponseEntity<List<Raza>> listarRazas()
            throws BadRequestException;
}