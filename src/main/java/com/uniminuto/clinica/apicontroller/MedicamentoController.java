package com.uniminuto.clinica.apicontroller;

import com.uniminuto.clinica.api.MedicamentoApi;
import com.uniminuto.clinica.entity.Medicamento;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.service.MedicamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controlador REST para manejar los endpoints de medicamentos.
 */
@RestController
public class MedicamentoController implements MedicamentoApi {

    @Autowired
    private MedicamentoService medicamento;

    @Override
    public ResponseEntity<List<Medicamento>> listarInventario() throws BadRequestException {
        return ResponseEntity.ok(medicamento.listarInventarioOrdenado());
    }
}
