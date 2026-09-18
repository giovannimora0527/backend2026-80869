package com.uniminuto.clinica.service;

import com.uniminuto.clinica.entity.Medicamento;

import java.util.List;

/**
 * Interfaz que define los servicios para manejar los medicamentos.
 */
public interface MedicamentoService {
    /**
     * Lista inventario ordenado de manera descendente.
     * @return lista de medicamentos
     */
    List<Medicamento> listarInventarioOrdenado();
}
