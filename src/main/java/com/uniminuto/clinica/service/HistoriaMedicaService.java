package com.uniminuto.clinica.service;

import com.uniminuto.clinica.models.HistoriaMedicaRq;
import com.uniminuto.clinica.models.MiRespuestaRS;

/**
 * Interfaz de servicio para la lógica de negocio de Historias Médicas.
 *
 * @author Yulian Estephania
 * @version 1.0
 */
public interface HistoriaMedicaService {

    MiRespuestaRS listar();

    MiRespuestaRS buscarPorId(Long id);

    MiRespuestaRS guardar(HistoriaMedicaRq request);
}