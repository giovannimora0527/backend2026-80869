
package com.uniminuto.clinica.api;

import com.uniminuto.clinica.entity.AnotacionHistoria;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.models.AnotacionHistoriaRq;
import com.uniminuto.clinica.models.MiRespuestaRS;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/anotacion-historia")
public interface AnotacionHistoriaApi {

    /**
     * Crear una anotación de historia clínica.
     *
     * @param anotacionHistoria información de la anotación.
     * @return respuesta de la operación.
     * @throws BadRequestException si los datos no son válidos.
     */
    @PostMapping(
            value = "/crear",
            produces = {"application/json"},
            consumes = {"application/json"}
    )
    ResponseEntity<MiRespuestaRS> crearAnotacionHistoria(
            @RequestBody AnotacionHistoriaRq anotacionHistoria
    ) throws BadRequestException;

    /**
     * Consultar las anotaciones de una historia clínica.
     *
     * @param historiaId identificador de la historia clínica.
     * @return anotaciones ordenadas de la más reciente a la más antigua.
     */
    @GetMapping(
            value = "/por-historia/{historiaId}",
            produces = {"application/json"}
    )
    ResponseEntity<List<AnotacionHistoria>> listarPorHistoria(
            @PathVariable Long historiaId
    );
}
