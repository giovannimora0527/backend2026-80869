package com.uniminuto.clinica.apicontroller;

import com.uniminuto.clinica.api.FormulaMedicaApi;
import com.uniminuto.clinica.entity.FormulaMedica;
import com.uniminuto.clinica.service.FormulaMedicaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controlador que implementa los endpoints de {@link FormulaMedicaApi}.
 * Delega la logica de negocio a la capa de servicio.
 */
@RestController
public class FormulaMedicaApiController implements FormulaMedicaApi {

    @Autowired
    private FormulaMedicaService formulaMedicaService;

    @Override
    public ResponseEntity<List<FormulaMedica>> obtenerTodasOrdenadas() {
        List<FormulaMedica> respuesta = formulaMedicaService.obtenerTodasOrdenadas();
        return ResponseEntity.ok(respuesta);
    }
}