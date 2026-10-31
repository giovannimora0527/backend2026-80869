package com.uniminuto.clinica.service;

import com.uniminuto.clinica.entity.FormulaMedica;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.models.FiltroPaginadoRq;
import com.uniminuto.clinica.models.FormulaResumenRs;
import com.uniminuto.clinica.models.FormulaRq;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.models.PageRs;

import java.util.List;

/**
 * Reglas de negocio de las fórmulas médicas.
 */
public interface FormulaService {

    /**
     * Lista todas las fórmulas (sin paginar), de la más reciente a la más antigua.
     *
     * @return fórmulas ordenadas por fecha de creación descendente.
     */
    List<FormulaMedica> findAllFormulas();

    /**
     * Listado paginado con filtros y ordenamiento para el DataTable (información resumida).
     *
     * @param filtro parámetros de página, orden y filtros.
     * @return página de filas resumidas; por defecto ordenada por fecha de creación descendente.
     */
    PageRs<FormulaResumenRs> listarFormulasPaginado(FiltroPaginadoRq filtro);

    /**
     * Consulta el detalle completo de una fórmula.
     *
     * @param id identificador de la fórmula.
     * @return la fórmula con todos sus datos.
     */
    FormulaMedica buscarFormulaPorId(Long id);

    /**
     * Registra una fórmula nueva validando mascota, cliente, veterinario, cita, fechas y valores.
     *
     * @param formulaRq datos de la fórmula.
     * @return mensaje de confirmación.
     * @throws BadRequestException si los datos no son válidos.
     */
    MiRespuestaRS guardarFormula(FormulaRq formulaRq) throws BadRequestException;

    /**
     * Edita los datos permitidos de una fórmula activa.
     *
     * @param formulaRq datos nuevos; debe incluir el id.
     * @return mensaje de confirmación.
     * @throws BadRequestException si los datos no son válidos.
     */
    MiRespuestaRS actualizarFormula(FormulaRq formulaRq) throws BadRequestException;

    /**
     * Activa o inactiva una fórmula (nunca se elimina físicamente).
     *
     * @param id     identificador de la fórmula.
     * @param activo {@code true} para activarla, {@code false} para inactivarla.
     * @return mensaje de confirmación.
     */
    MiRespuestaRS cambiarEstadoFormula(Long id, Boolean activo);
}
