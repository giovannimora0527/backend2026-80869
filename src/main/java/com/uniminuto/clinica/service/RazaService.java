package com.uniminuto.clinica.service;

import com.uniminuto.clinica.entity.Raza;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.models.RazaRq;

import java.util.List;

/**
 * Interfaz que define los servicios de negocio para la gestión de Razas.
 *
 * @author Yulian Estefanía Montealegre
 */
public interface RazaService {

    /**
     * Crea una nueva raza en el sistema.
     *
     * @param razaRq Datos de la raza a crear.
     * @return Respuesta estandarizada con el resultado.
     * @throws BadRequestException Si los datos son inválidos o la raza ya existe.
     */
    MiRespuestaRS crearRazaNueva(RazaRq razaRq) throws BadRequestException;

    /**
     * Obtiene el listado completo de razas registradas.
     *
     * @return Lista de razas.
     * @throws BadRequestException Si ocurre un error en la consulta.
     */
    List<Raza> listarRazas() throws BadRequestException;
}