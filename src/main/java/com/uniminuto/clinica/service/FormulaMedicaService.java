package com.uniminuto.clinica.service;

import com.uniminuto.clinica.entity.FormulaMedica;
import com.uniminuto.clinica.models.FormulaMedicaRq;
import com.uniminuto.clinica.models.MiRespuestaRS;

import java.util.List;

/**
 * Interfaz que define los servicios de negocio para las Fórmulas Médicas.
 *
 * @author Yulian Estephania Montealegre
 */
public interface FormulaMedicaService {

    /**
     * Obtiene todas las fórmulas médicas registradas ordenadas descendentemente por fecha.
     *
     * @return Lista de fórmulas médicas.
     */
    List<FormulaMedica> obtenerTodas();

    /**
     * Crea una nueva fórmula médica con validaciones de cita activa y medicamento existente.
     *
     * @param request Datos de la fórmula médica a registrar.
     * @return Objeto de respuesta estandarizada.
     */
    MiRespuestaRS crearFormula(FormulaMedicaRq request);

    /**
     * Actualiza una fórmula médica existente.
     *
     * @param id Identificador de la fórmula a actualizar.
     * @param request Datos actualizados.
     * @return Objeto de respuesta estandarizada.
     */
    MiRespuestaRS actualizarFormula(Long id, FormulaMedicaRq request);
}