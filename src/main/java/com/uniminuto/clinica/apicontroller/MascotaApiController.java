package com.uniminuto.clinica.apicontroller;

import com.uniminuto.clinica.api.MascotaApi;
import com.uniminuto.clinica.entity.Mascota;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.models.MascotaRq;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.service.MascotaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST que implementa los endpoints de MascotaApi
 * para la gestión y consulta de pacientes veterinarios.
 *
 * @author Yulian Estefanía Montealegre
 */
@RestController
@RequestMapping("/mascotas")
public class MascotaApiController implements MascotaApi {

    @Autowired
    private MascotaService mascotaService;

    /**
     * Obtiene el listado completo de mascotas registradas.
     */
    @GetMapping
    @Override
    public ResponseEntity<List<Mascota>> listarMascotas() throws BadRequestException {
        return ResponseEntity.ok(mascotaService.listarMascotas());
    }

    /**
     * Obtiene el listado de mascotas ordenadas de forma ascendente o descendente.
     */
    @GetMapping("/ordenado")
    @Override
    public ResponseEntity<List<Mascota>> listarMascotasOrdenado(@RequestParam boolean ascendente) throws BadRequestException {
        return ResponseEntity.ok(mascotaService.listarMascotasOrdenado(ascendente));
    }

    /**
     * Busca una mascota por su nombre exacto.
     */
    @GetMapping("/buscar-por-nombre")
    @Override
    public ResponseEntity<List<Mascota>> buscarMascotaPorNombre(@RequestParam String nombre) throws BadRequestException {
        return ResponseEntity.ok(mascotaService.findByNombreMascota(nombre).map(List::of).orElseGet(List::of));
    }

    /**
     * Busca mascotas asociadas a un cliente propietario específico.
     */
    @GetMapping("/cliente/{clienteId}")
    @Override
    public ResponseEntity<List<Mascota>> buscarMascotaPorCliente(@PathVariable Long clienteId) throws BadRequestException {
        return ResponseEntity.ok(mascotaService.buscarMascotasPorCliente(clienteId));
    }

    /**
     * Busca mascotas pertenecientes a una raza específica.
     */
    @GetMapping("/raza/{razaId}")
    @Override
    public ResponseEntity<List<Mascota>> buscarMascotaPorRaza(@PathVariable Integer razaId) throws BadRequestException {
        return ResponseEntity.ok(mascotaService.buscarMascotasPorRaza(razaId));
    }

    /**
     * Registra una nueva mascota en el sistema.
     */
    @PostMapping
    @Override
    public ResponseEntity<MiRespuestaRS> guardarMascota(@RequestBody MascotaRq mascotaRq) throws BadRequestException {
        return ResponseEntity.ok(mascotaService.guardarMascota(mascotaRq));
    }

    /**
     * Actualiza la información de una mascota existente.
     */
    @PutMapping
    @Override
    public ResponseEntity<MiRespuestaRS> actualizarMascota(@RequestBody MascotaRq mascotaRq) throws BadRequestException {
        return ResponseEntity.ok(mascotaService.actualizarMascota(mascotaRq));
    }
}