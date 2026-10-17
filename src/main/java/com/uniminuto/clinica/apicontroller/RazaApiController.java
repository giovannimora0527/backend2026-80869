package com.uniminuto.clinica.apicontroller;

import com.uniminuto.clinica.api.RazaApi;
import com.uniminuto.clinica.entity.Raza;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.models.RazaRq;
import com.uniminuto.clinica.service.RazaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST que implementa los endpoints definidos en RazaApi
 * para la gestión y administración de razas de mascotas.
 *
 * @author Yulian Estefanía Montealegre
 */
@RestController
@RequestMapping("/razas")
public class RazaApiController implements RazaApi {

    @Autowired
    private RazaService razaService;

    /**
     * Registra una nueva raza en el sistema.
     *
     * @param razaRq Objeto DTO con los datos de la raza.
     * @return Respuesta estandarizada con el resultado.
     * @throws BadRequestException Si los datos son inválidos o la raza ya existe.
     */
    @PostMapping
    @Override
    public ResponseEntity<MiRespuestaRS> guardarRaza(@RequestBody RazaRq razaRq) throws BadRequestException {
        return ResponseEntity.ok(this.razaService.crearRazaNueva(razaRq));
    }

    /**
     * Obtiene el listado completo de razas registradas.
     *
     * @return Lista de entidades Raza.
     * @throws BadRequestException Si ocurre un error en la consulta.
     */
    @GetMapping
    @Override
    public ResponseEntity<List<Raza>> listarRazas() throws BadRequestException {
        return ResponseEntity.ok(this.razaService.listarRazas());
    }
}