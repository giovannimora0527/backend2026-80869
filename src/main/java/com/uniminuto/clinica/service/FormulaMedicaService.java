package com.uniminuto.clinica.service;

import com.uniminuto.clinica.models.FormulaMedicaRS;
import com.uniminuto.clinica.exception.BadRequestException;

import java.util.List;


public interface FormulaMedicaService {

    /**
     * Lista todas las fórmulas médicas del inventario, ordenadas por fecha
     * de creación de la más reciente a la más antigua
     */
    List<FormulaMedicaRS> listar() throws BadRequestException;
}