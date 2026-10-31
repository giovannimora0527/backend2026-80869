package com.uniminuto.clinica.apicontroller;

import com.uniminuto.clinica.api.AnotacionHistoriaApi;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.models.AnotacionHistoriaRq;
import com.uniminuto.clinica.models.AnotacionHistoriaRs;
import com.uniminuto.clinica.models.FiltroPaginadoRq;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.models.PageRs;
import com.uniminuto.clinica.service.AnotacionHistoriaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST de anotaciones. Solo recibe la petición y delega en
 * {@link AnotacionHistoriaService}; no contiene reglas de negocio.
 */
@RestController
public class AnotacionHistoriaApiController implements AnotacionHistoriaApi {

    private final AnotacionHistoriaService anotacionHistoriaService;

    /**
     * Crea el controlador con su servicio.
     *
     * @param anotacionHistoriaService servicio de anotaciones.
     */
    public AnotacionHistoriaApiController(AnotacionHistoriaService anotacionHistoriaService) {
        this.anotacionHistoriaService = anotacionHistoriaService;
    }

    @Override
    public ResponseEntity<MiRespuestaRS> crearAnotacionHistoria(AnotacionHistoriaRq anotacionHistoria)
            throws BadRequestException {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(anotacionHistoriaService.crearAnotacionHistoria(anotacionHistoria));
    }

    @Override
    public ResponseEntity<PageRs<AnotacionHistoriaRs>> listarPorHistoria(Long historiaId, FiltroPaginadoRq filtro) {
        return ResponseEntity.ok(anotacionHistoriaService.listarPorHistoria(historiaId, filtro));
    }

    @Override
    public ResponseEntity<PageRs<AnotacionHistoriaRs>> listarPorMascota(Integer mascotaId, FiltroPaginadoRq filtro) {
        return ResponseEntity.ok(anotacionHistoriaService.listarPorMascota(mascotaId, filtro));
    }

    @Override
    public ResponseEntity<AnotacionHistoriaRs> buscarPorId(Long id) {
        return ResponseEntity.ok(anotacionHistoriaService.buscarPorId(id));
    }
}
