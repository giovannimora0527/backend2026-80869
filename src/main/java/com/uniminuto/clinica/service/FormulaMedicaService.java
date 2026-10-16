package com.uniminuto.clinica.service;

import com.uniminuto.clinica.entity.FormulaMedica;

import java.util.List;

/**
 * Interfaz del servicio para gestionar fórmulas médicas.
 * @author Yulian Montealegre
 */
public interface FormulaMedicaService {

    /**
     * Obtiene todas las fórmulas médicas.
     */
    List<FormulaMedica> obtenerTodas();

    /**
     * Crea una nueva fórmula médica.
     */
    FormulaMedica crear(FormulaMedica formula);

    /**
     * Actualiza una fórmula médica existente.
     */
    FormulaMedica actualizar(Long id, FormulaMedica formula);
}