package com.uniminuto.clinica.service;

import com.uniminuto.clinica.models.AnotacionHistoriaRq;
import com.uniminuto.clinica.models.AnotacionHistoriaRs;
import com.uniminuto.clinica.models.FiltroPaginadoRq;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.models.PageRs;

/**
 * Reglas de negocio de las anotaciones de una historia clínica.
 */
public interface AnotacionHistoriaService {

    /**
     * Agrega una anotación a una historia clínica existente.
     *
     * @param anotacionHistoriaRq datos de la anotación (historia o cita, veterinario y textos).
     * @return mensaje de confirmación.
     */
    MiRespuestaRS crearAnotacionHistoria(AnotacionHistoriaRq anotacionHistoriaRq);

    /**
     * Histórico paginado de anotaciones de una historia clínica.
     *
     * @param historiaId identificador de la historia.
     * @param filtro     página, orden y filtros (q, medicoId, desde, hasta).
     * @return página de anotaciones, de la más reciente a la más antigua por defecto.
     */
    PageRs<AnotacionHistoriaRs> listarPorHistoria(Long historiaId, FiltroPaginadoRq filtro);

    /**
     * Histórico paginado de todas las anotaciones de una mascota (de todas sus historias).
     *
     * @param mascotaId identificador de la mascota.
     * @param filtro    página, orden y filtros (q, medicoId, desde, hasta).
     * @return página de anotaciones, de la más reciente a la más antigua por defecto.
     */
    PageRs<AnotacionHistoriaRs> listarPorMascota(Integer mascotaId, FiltroPaginadoRq filtro);

    /**
     * Consulta el detalle de una anotación (incluye las fórmulas de su consulta).
     *
     * @param id identificador de la anotación.
     * @return la anotación con su detalle.
     */
    AnotacionHistoriaRs buscarPorId(Long id);
}
