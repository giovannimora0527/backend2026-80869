package com.uniminuto.clinica.api;

import com.uniminuto.clinica.entity.Mascota;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.models.MascotaRq;
import com.uniminuto.clinica.models.MiRespuestaRS;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * API para la gestión de mascotas.
 * Define los endpoints REST para registrar, consultar y actualizar pacientes.
 *
 * @author Yulian Estefanía Montealegre
 */
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/mascota")
public interface MascotaApi {

    /**
     * Obtiene el listado completo de mascotas registradas.
     *
     * @return Lista de mascotas.
     * @throws BadRequestException Si ocurre un error en la solicitud.
     */
    @GetMapping(value = "/listar", produces = {"application/json"})
    ResponseEntity<List<Mascota>> listarMascotas() throws BadRequestException;

    /**
     * Obtiene el listado de mascotas ordenadas de forma ascendente o descendente.
     *
     * @param ascendente Booleano que indica el sentido del ordenamiento.
     * @return Lista de mascotas ordenadas.
     * @throws BadRequestException Si ocurre un error en la solicitud.
     */
    @GetMapping(value = "/listar-ordenado", produces = {"application/json"})
    ResponseEntity<List<Mascota>> listarMascotasOrdenado(
            @RequestParam boolean ascendente) throws BadRequestException;

    /**
     * Busca mascotas que coincidan parcialmente con un nombre.
     *
     * @param nombre Nombre o fragmento del nombre de la mascota.
     * @return Lista de mascotas encontradas.
     * @throws BadRequestException Si el parámetro es nulo o inválido.
     */
    @GetMapping(value = "/buscar-nombre", produces = {"application/json"})
    ResponseEntity<List<Mascota>> buscarMascotaPorNombre(
            @RequestParam String nombre) throws BadRequestException;

    /**
     * Busca las mascotas asociadas a un cliente propietario específico.
     *
     * @param clienteId Identificador único del cliente.
     * @return Lista de mascotas del cliente.
     * @throws BadRequestException Si el ID del cliente no es válido.
     */
    @GetMapping(value = "/buscar-by-cliente", produces = {"application/json"})
    ResponseEntity<List<Mascota>> buscarMascotaPorCliente(
            @RequestParam Long clienteId) throws BadRequestException;

    /**
     * Busca las mascotas pertenecientes a una raza específica.
     *
     * @param razaId Identificador de la raza.
     * @return Lista de mascotas de dicha raza.
     * @throws BadRequestException Si el ID de raza no es válido.
     */
    @GetMapping(value = "/buscar-by-raza", produces = {"application/json"})
    ResponseEntity<List<Mascota>> buscarMascotaPorRaza(
            @RequestParam Integer razaId) throws BadRequestException;

    /**
     * Registra una nueva mascota en el sistema.
     *
     * @param mascotaRq Datos de la mascota a registrar.
     * @return Respuesta estandarizada con el resultado de la operación.
     * @throws BadRequestException Si faltan campos obligatorios.
     */
    @PostMapping(value = "/guardar", produces = {"application/json"}, consumes = {"application/json"})
    ResponseEntity<MiRespuestaRS> guardarMascota(
            @RequestBody MascotaRq mascotaRq) throws BadRequestException;

    /**
     * Actualiza la información de una mascota existente.
     *
     * @param mascotaRq Datos actualizados de la mascota.
     * @return Respuesta estandarizada con el resultado.
     * @throws BadRequestException Si ocurre un error de validación.
     */
    @PostMapping(value = "/actualizar", produces = {"application/json"}, consumes = {"application/json"})
    ResponseEntity<MiRespuestaRS> actualizarMascota(
            @RequestBody MascotaRq mascotaRq) throws BadRequestException;
}