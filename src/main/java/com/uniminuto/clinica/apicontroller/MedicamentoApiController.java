package com.uniminuto.clinica.apicontroller;

import com.uniminuto.clinica.api.MedicamentoApi;
import com.uniminuto.clinica.entity.Medicamento;
import com.uniminuto.clinica.service.MedicamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controlador REST que implementa los endpoints de Medicamentos.
 *
 * @author Yulian Estephania Montealegre
 */
@RestController
public class MedicamentoApiController implements MedicamentoApi {

    @Autowired
    private MedicamentoService medicamentoService;

    @Override
    public ResponseEntity<List<Medicamento>> listarMedicamentos() {
        return ResponseEntity.ok(medicamentoService.listarMedicamentos());
    }

    @Override
    public ResponseEntity<Medicamento> buscarMedicamentoPorId(Long id) {
        return ResponseEntity.ok(medicamentoService.buscarMedicamentoPorId(id));
    }
}