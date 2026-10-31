package com.uniminuto.clinica.apicontroller;

import com.uniminuto.clinica.api.FormulaMedicaApi;
import com.uniminuto.clinica.entity.FormulaMedica;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.models.EstadoRq;
import com.uniminuto.clinica.models.FiltroPaginadoRq;
import com.uniminuto.clinica.models.FormulaResumenRs;
import com.uniminuto.clinica.models.FormulaRq;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.models.PageRs;
import com.uniminuto.clinica.service.FormulaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controlador REST de fórmulas médicas. Solo recibe la petición y delega en
 * {@link FormulaService}; no contiene reglas de negocio.
 */
@RestController
public class FormulaMedicaApiController implements FormulaMedicaApi {

    private final FormulaService formulaService;

    /**
     * Crea el controlador con su servicio.
     *
     * @param formulaService servicio de fórmulas.
     */
    public FormulaMedicaApiController(FormulaService formulaService) {
        this.formulaService = formulaService;
    }

    @Override
    public ResponseEntity<List<FormulaMedica>> listarFormulas() {
        return ResponseEntity.ok(formulaService.findAllFormulas());
    }

    @Override
    public ResponseEntity<PageRs<FormulaResumenRs>> listarFormulasPaginado(FiltroPaginadoRq filtro) {
        return ResponseEntity.ok(formulaService.listarFormulasPaginado(filtro));
    }

    @Override
    public ResponseEntity<FormulaMedica> buscarFormulaPorId(Long id) {
        return ResponseEntity.ok(formulaService.buscarFormulaPorId(id));
    }

    @Override
    public ResponseEntity<MiRespuestaRS> guardarFormula(FormulaRq formulaRq) throws BadRequestException {
        return ResponseEntity.status(HttpStatus.CREATED).body(formulaService.guardarFormula(formulaRq));
    }

    @Override
    public ResponseEntity<MiRespuestaRS> actualizarFormula(FormulaRq formulaRq) throws BadRequestException {
        return ResponseEntity.ok(formulaService.actualizarFormula(formulaRq));
    }

    @Override
    public ResponseEntity<MiRespuestaRS> editarFormula(Long id, FormulaRq formulaRq) {
        formulaRq.setId(id);
        return ResponseEntity.ok(formulaService.actualizarFormula(formulaRq));
    }

    @Override
    public ResponseEntity<MiRespuestaRS> cambiarEstadoFormula(Long id, EstadoRq estadoRq) {
        Boolean activo = estadoRq == null ? null : estadoRq.getActivo();
        return ResponseEntity.ok(formulaService.cambiarEstadoFormula(id, activo));
    }
}
