
package com.uniminuto.clinica.apicontroller;

import com.uniminuto.clinica.api.FormulaMedicaApi;
import com.uniminuto.clinica.entity.FormulaMedica;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.models.FormulaMedicaRq;
import com.uniminuto.clinica.service.FormulaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class FormulaMedicaApiController implements FormulaMedicaApi {

    @Autowired
    private FormulaService formulaService;

    @Override
    public ResponseEntity<List<FormulaMedica>> listarFormulas()
            throws BadRequestException {
        return ResponseEntity.ok(
                formulaService.findAllFormulas()
        );
    }

    @Override
    public ResponseEntity<FormulaMedica> consultarFormula(Long id)
            throws BadRequestException {
        return ResponseEntity.ok(
                formulaService.consultarFormula(id)
        );
    }

    @Override
    public ResponseEntity<FormulaMedica> crearFormula(
            FormulaMedicaRq formulaRq
    ) throws BadRequestException {
        return ResponseEntity.ok(
                formulaService.crearFormula(formulaRq)
        );
    }

    @Override
    public ResponseEntity<FormulaMedica> actualizarFormula(
            Long id,
            FormulaMedicaRq formulaRq
    ) throws BadRequestException {
        return ResponseEntity.ok(
                formulaService.actualizarFormula(id, formulaRq)
        );
    }
}
