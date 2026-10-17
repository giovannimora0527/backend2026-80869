package com.uniminuto.clinica.service;

import com.uniminuto.clinica.entity.Mascota;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.models.MascotaRq;
import com.uniminuto.clinica.models.MiRespuestaRS;

import java.util.Optional;
import java.util.List;

/**
 * Interfaz que define los servicios de negocio para la gestión de Mascotas.
 *
 * @author Yulian Estefanía Montealegre
 */
public interface MascotaService {

    /**
     * Obtiene el listado completo de mascotas.
     */
    List<Mascota> listarMascotas();

    /**
     * Obtiene el listado de mascotas ordenadas de forma ascendente o descendente.
     */
    List<Mascota> listarMascotasOrdenado(boolean ascendente);

    /**
     * Busca una mascota por su nombre.
     */
    Optional<Mascota> findByNombreMascota(String nombreMascota);

    /**
     * Busca mascotas por ID de cliente propietario.
     */
    List<Mascota> buscarMascotasPorCliente(Long clienteId) throws BadRequestException;

    /**
     * Busca mascotas por ID de raza.
     */
    List<Mascota> buscarMascotasPorRaza(Integer razaId) throws BadRequestException;

    /**
     * Guarda una nueva mascota en el sistema.
     */
    MiRespuestaRS guardarMascota(MascotaRq mascotaRq) throws BadRequestException;

    /**
     * Actualiza una mascota existente.
     */
    MiRespuestaRS actualizarMascota(MascotaRq mascotaRq) throws BadRequestException;

}