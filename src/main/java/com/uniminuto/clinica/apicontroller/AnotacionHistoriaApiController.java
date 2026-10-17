package com.uniminuto.clinica.apicontroller;

import com.uniminuto.clinica.api.AnotacionHistoriaApi;
import com.uniminuto.clinica.entity.AnotacionHistoria;
import com.uniminuto.clinica.models.AnotacionHistoriaRq;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.service.AnotacionHistoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST que implementa los endpoints definidos en AnotacionHistoriaApi
 * para la gestión de notas, evolución y seguimiento dentro de las historias clínicas.
 *
 * @author Yulian Estefanía Montealegre
 * @version 1.0
 */
@RestController
@RequestMapping("/anotaciones")
public class AnotacionHistoriaApiController implements AnotacionHistoriaApi {

    @Autowired
    private AnotacionHistoriaService anotacionHistoriaService;

    /**
     * Registra una nueva anotación o nota de evolución en una historia clínica.
     *
     * @param anotacionHistoriaRq Objeto DTO con la información de la anotación a crear.
     * @return Respuesta estandarizada {@link MiRespuestaRS} con el estado de la operación.
     */
    @PostMapping
    @Override
    public ResponseEntity<MiRespuestaRS> crearAnotacionHistoria(@RequestBody AnotacionHistoriaRq anotacionHistoriaRq) {
        return ResponseEntity.ok(anotacionHistoriaService.crearAnotacionHistoria(anotacionHistoriaRq));
    }

    /**
     * Obtiene el listado de anotaciones asociadas a una historia clínica específica.
     *
     * @param historiaId Identificador único de la historia clínica.
     * @return Lista de entidades {@link AnotacionHistoria} registradas en dicha historia.
     */
    @GetMapping("/historia/{historiaId}")
    @Override
    public ResponseEntity<List<AnotacionHistoria>> listarPorHistoria(@PathVariable Long historiaId) {
        return ResponseEntity.ok(anotacionHistoriaService.listarAnotacionesPorHistoria(historiaId));
    }
}