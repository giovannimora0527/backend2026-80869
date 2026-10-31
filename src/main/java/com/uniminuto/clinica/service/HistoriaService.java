package com.uniminuto.clinica.service;

import com.uniminuto.clinica.entity.Cita;
import com.uniminuto.clinica.entity.HistoriaMedica;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.models.FiltroPaginadoRq;
import com.uniminuto.clinica.models.HistoriaMedicaRq;
import com.uniminuto.clinica.models.HistoriaMedicaRs;
import com.uniminuto.clinica.models.HistoriaResumenRs;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.models.PageRs;

import java.util.List;

/**
 * Reglas de negocio de las historias clínicas (una por consulta realizada).
 */
public interface HistoriaService {

    /**
     * Lista todas las historias con sus anotaciones y fórmulas (sin paginar).
     *
     * @return historias con su detalle.
     */
    List<HistoriaMedicaRs> getAllHistorias();

    /**
     * Listado paginado con filtros y ordenamiento para el DataTable.
     *
     * @param filtro parámetros de página, orden y filtros.
     * @return una página de filas resumidas; por defecto ordenada por fecha de consulta descendente.
     */
    PageRs<HistoriaResumenRs> listarHistoriasPaginado(FiltroPaginadoRq filtro);

    /**
     * Consulta el detalle de una historia con sus anotaciones y fórmulas.
     *
     * @param id identificador de la historia.
     * @return historia, anotaciones y fórmulas.
     */
    HistoriaMedicaRs buscarHistoriaPorId(Long id);

    /**
     * Registra la historia clínica de una consulta.
     *
     * @param historiaMedicaRq datos de la consulta.
     * @return mensaje de confirmación.
     * @throws BadRequestException si los datos no son válidos.
     */
    MiRespuestaRS guardarHistoria(HistoriaMedicaRq historiaMedicaRq) throws BadRequestException;

    /**
     * Edita el motivo, diagnóstico y observaciones de una historia.
     *
     * @param historiaMedicaRq datos nuevos; debe incluir el id.
     * @return mensaje de confirmación.
     * @throws BadRequestException si los datos no son válidos.
     */
    MiRespuestaRS actualizarHistoria(HistoriaMedicaRq historiaMedicaRq) throws BadRequestException;

    /**
     * Devuelve la historia de una cita atendida o la crea si todavía no existe.
     * La usa el servicio de citas cuando una cita pasa a Atendida.
     *
     * @param cita cita atendida.
     * @return la historia asociada a la cita.
     */
    HistoriaMedica asegurarHistoriaDeCita(Cita cita);
}
