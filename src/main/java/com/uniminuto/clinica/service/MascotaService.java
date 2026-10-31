package com.uniminuto.clinica.service;

import com.uniminuto.clinica.entity.Mascota;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.models.MascotaRq;
import com.uniminuto.clinica.models.MiRespuestaRS;

import java.util.Optional;
import java.util.List;

public interface MascotaService {

    List<Mascota> listarMascotas();

    List<Mascota> listarMascotasOrdenado(boolean ascendente);

    Optional<Mascota> findByNombreMascota(String nombreMascota);

    /**
     * Consulta una mascota por su id.
     *
     * @param id identificador de la mascota.
     * @return la mascota con su cliente y raza.
     */
    Mascota buscarMascotaPorId(Integer id);

    /**
     * Lista las mascotas de un cliente (para el combo dependiente Cliente → Mascota).
     *
     * @param clienteId identificador del cliente.
     * @return mascotas del cliente ordenadas por nombre.
     * @throws BadRequestException si el id es nulo; 404 si el cliente no existe.
     */
    List<Mascota> buscarMascotasPorCliente(Long clienteId) throws BadRequestException;

    List<Mascota> buscarMascotasPorRaza(Integer razaId) throws BadRequestException;

    MiRespuestaRS guardarMascota(MascotaRq mascotaRq) throws BadRequestException;

    MiRespuestaRS actualizarMascota(MascotaRq mascotaRq) throws BadRequestException;

}

