
package com.uniminuto.clinica.service;

import com.uniminuto.clinica.entity.FormulaMedica;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.models.FormulaMedicaRq;

import java.util.List;

public interface FormulaService {

    List<FormulaMedica> findAllFormulas();

    FormulaMedica consultarFormula(Long id)
            throws BadRequestException;

    FormulaMedica crearFormula(FormulaMedicaRq formulaRq)
            throws BadRequestException;

    FormulaMedica actualizarFormula(
            Long id,
            FormulaMedicaRq formulaRq
    ) throws BadRequestException;
}
