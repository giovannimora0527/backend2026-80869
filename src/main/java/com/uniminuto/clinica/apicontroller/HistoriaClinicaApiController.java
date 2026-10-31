package com.uniminuto.clinica.apicontroller;

import com.uniminuto.clinica.api.HistoriaApi;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.models.FiltroPaginadoRq;
import com.uniminuto.clinica.models.HistoriaMedicaRq;
import com.uniminuto.clinica.models.HistoriaMedicaRs;
import com.uniminuto.clinica.models.HistoriaResumenRs;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.models.PageRs;
import com.uniminuto.clinica.service.HistoriaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controlador REST de historias clínicas. Solo recibe la petición y delega en
 * {@link HistoriaService}; no contiene reglas de negocio.
 */
@RestController
public class HistoriaClinicaApiController implements HistoriaApi {

    private final HistoriaService historiaService;

    /**
     * Crea el controlador con su servicio.
     *
     * @param historiaService servicio de historias clínicas.
     */
    public HistoriaClinicaApiController(HistoriaService historiaService) {
        this.historiaService = historiaService;
    }

    @Override
    public ResponseEntity<List<HistoriaMedicaRs>> listarAllHIstorias() throws BadRequestException {
        return ResponseEntity.ok(historiaService.getAllHistorias());
    }

    @Override
    public ResponseEntity<PageRs<HistoriaResumenRs>> listarHistoriasPaginado(FiltroPaginadoRq filtro) {
        return ResponseEntity.ok(historiaService.listarHistoriasPaginado(filtro));
    }

    @Override
    public ResponseEntity<HistoriaMedicaRs> buscarHistoriaPorId(Long id) {
        return ResponseEntity.ok(historiaService.buscarHistoriaPorId(id));
    }

    @Override
    public ResponseEntity<MiRespuestaRS> guardarHistoria(HistoriaMedicaRq historiaMedicaRq)
            throws BadRequestException {
        return ResponseEntity.status(HttpStatus.CREATED).body(historiaService.guardarHistoria(historiaMedicaRq));
    }

    @Override
    public ResponseEntity<MiRespuestaRS> actualizarHistoria(HistoriaMedicaRq historiaMedicaRq)
            throws BadRequestException {
        return ResponseEntity.ok(historiaService.actualizarHistoria(historiaMedicaRq));
    }

    @Override
    public ResponseEntity<MiRespuestaRS> editarHistoria(Long id, HistoriaMedicaRq historiaMedicaRq) {
        historiaMedicaRq.setId(id);
        return ResponseEntity.ok(historiaService.actualizarHistoria(historiaMedicaRq));
    }
}
