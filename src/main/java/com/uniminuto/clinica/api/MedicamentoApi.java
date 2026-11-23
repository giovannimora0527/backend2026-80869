package com.uniminuto.clinica.api;

import com.uniminuto.clinica.entity.Medicamento;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.models.InventarioPreciosRq;
import com.uniminuto.clinica.models.MedicamentoLoteRq;
import com.uniminuto.clinica.repository.MedicamentoRepository.InventarioResumen;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/medicamento")
public interface MedicamentoApi {

    @GetMapping(value = "/listar", produces = "application/json")
    ResponseEntity<List<Medicamento>> listarMedicamentos()
            throws BadRequestException;

    @GetMapping(value = "/inventario", produces = "application/json")
    ResponseEntity<List<InventarioResumen>> listarInventario();

    @PutMapping(value = "/{id}/precio-stock", consumes = "application/json")
    ResponseEntity<Void> actualizarPrecioYStockMinimo(
            @PathVariable("id") Long id,
            @RequestBody InventarioPreciosRq datos
    ) throws BadRequestException;

    @PostMapping(value = "/{id}/lotes", consumes = "application/json")
    ResponseEntity<Void> registrarLote(
            @PathVariable("id") Long id,
            @RequestBody MedicamentoLoteRq datos
    ) throws BadRequestException;
}