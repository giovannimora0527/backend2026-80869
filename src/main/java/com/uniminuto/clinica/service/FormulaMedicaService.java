package com.uniminuto.clinica.service;

import com.uniminuto.clinica.entity.FormulaMedica;

import java.util.List;

/**
 * Interfaz que define los servicios para manejar las fórmulas médicas.
 */
public interface FormulaMedicaService {
    /**
     * Lista inventario.
     * @return lista
     * @throws BadRequestException ex
     */
    List<FormulaMedica> listarInventarioOrdenado();
}
