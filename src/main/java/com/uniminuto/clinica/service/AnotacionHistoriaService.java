package com.uniminuto.clinica.service;

import com.uniminuto.clinica.entity.AnotacionHistoria;
import com.uniminuto.clinica.models.AnotacionHistoriaRq;
import com.uniminuto.clinica.models.MiRespuestaRS;
import java.util.List;

/**
 * Interfaz de servicio para la gestión de anotaciones en las historias clínicas.
 *
 * @author Yulian Estephania Montealegre
 * @version 1.0
 */
public interface AnotacionHistoriaService {

    /**
     * Crea una nueva anotación vinculada a una historia clínica existente.
     *
     * @param anotacionHistoriaRq Datos de la anotación a registrar.
     * @return Respuesta estandarizada con el resultado de la operación.
     */
    MiRespuestaRS crearAnotacionHistoria(AnotacionHistoriaRq anotacionHistoriaRq);

    /**
     * Consulta todas las anotaciones asociadas a una historia clínica específica, ordenadas por fecha DESC.
     *
     * @param historiaId Identificador de la historia clínica.
     * @return Lista de anotaciones médicas encontradas.
     */
    List<AnotacionHistoria> listarAnotacionesPorHistoria(Long historiaId);
}