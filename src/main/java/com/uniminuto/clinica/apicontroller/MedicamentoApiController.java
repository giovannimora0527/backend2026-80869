package com.uniminuto.clinica.apicontroller;

import com.uniminuto.clinica.api.MedicamentoApi;
import com.uniminuto.clinica.entity.Medicamento;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.models.InventarioPreciosRq;
import com.uniminuto.clinica.models.MedicamentoLoteRq;
import com.uniminuto.clinica.repository.MedicamentoRepository.InventarioResumen;
import com.uniminuto.clinica.service.MedicamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class MedicamentoApiController implements MedicamentoApi {

    @Autowired
    private MedicamentoService medicamentoService;

    @Override
    public ResponseEntity<List<Medicamento>> listarMedicamentos()
            throws BadRequestException {
        return ResponseEntity.ok(medicamentoService.listarMedicamentos());
    }

    @Override
    public ResponseEntity<List<InventarioResumen>> listarInventario() {
        return ResponseEntity.ok(medicamentoService.listarInventario());
    }

    @Override
    public ResponseEntity<Void> actualizarPrecioYStockMinimo(
            Long id,
            InventarioPreciosRq datos
    ) throws BadRequestException {
        medicamentoService.actualizarPrecioYStockMinimo(id, datos);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Void> registrarLote(
            Long id,
            MedicamentoLoteRq datos
    ) throws BadRequestException {
        medicamentoService.registrarLote(id, datos);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}