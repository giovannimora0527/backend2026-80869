package com.uniminuto.clinica.service;

import com.uniminuto.clinica.entity.Medicamento;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.models.InventarioPreciosRq;
import com.uniminuto.clinica.models.MedicamentoLoteRq;
import com.uniminuto.clinica.repository.MedicamentoRepository.InventarioResumen;

import java.util.List;

public interface MedicamentoService {

    List<Medicamento> listarMedicamentos() throws BadRequestException;

    List<InventarioResumen> listarInventario();

    void actualizarPrecioYStockMinimo(Long id, InventarioPreciosRq datos)
            throws BadRequestException;

    void registrarLote(Long id, MedicamentoLoteRq datos)
            throws BadRequestException;
}