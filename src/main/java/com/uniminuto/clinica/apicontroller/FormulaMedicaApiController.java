package com.uniminuto.clinica.apicontroller;

// Importamos la API que vamos a implementar
import com.uniminuto.clinica.api.FormulaMedicaApi;
// Importamos la entidad que vamos a devolver
import com.uniminuto.clinica.entity.FormulaMedica;
// Importamos el servicio que contiene la lógica de negocio
import com.uniminuto.clinica.service.FormulaMedicaService;
// @Autowired para inyección de dependencias (aunque usaremos constructor)
import org.springframework.beans.factory.annotation.Autowired;
// ResponseEntity para devolver respuestas HTTP estandarizadas
import org.springframework.http.ResponseEntity;
// @RestController marca esta clase como controlador REST
import org.springframework.web.bind.annotation.RestController;
import java.util.List;


@RestController
public class FormulaMedicaApiController implements FormulaMedicaApi {


    @Autowired
    private FormulaMedicaService formulaMedicaService;

    /**
     * {@inheritDoc}
     *

     */
    @Override
    public ResponseEntity<List<FormulaMedica>> obtenerTodasOrdenadas() {
        // Delegamos la consulta al servicio
        List<FormulaMedica> respuesta = formulaMedicaService.obtenerTodasOrdenadas();
        // Devolvemos HTTP 200 OK con la lista en el body
        return ResponseEntity.ok(respuesta);
    }
}