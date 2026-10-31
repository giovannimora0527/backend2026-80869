package com.uniminuto.clinica.api;

import com.uniminuto.clinica.models.EstadoRq;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.models.UsuarioRS;
import com.uniminuto.clinica.models.UsuarioRq;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * API REST para la gestión de usuarios del sistema.
 * No hay borrado: los usuarios se desactivan con {@code PATCH /{id}/estado}.
 * Nunca se devuelve el hash de la contraseña.
 */
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/usuario")
public interface UsuarioApi {

    /** Lista todos los usuarios (activos e inactivos). */
    @GetMapping(value = "/listar", produces = {"application/json"})
    ResponseEntity<List<UsuarioRS>> listarUsuarios();

    /** Consulta un usuario por id (404 si no existe). */
    @GetMapping(value = "/{id}", produces = {"application/json"})
    ResponseEntity<UsuarioRS> buscarUsuarioPorId(@PathVariable("id") Long id);

    /** Crea un usuario (400 datos inválidos; 409 usuario o correo duplicado). */
    @PostMapping(value = "/crear", produces = {"application/json"}, consumes = {"application/json"})
    ResponseEntity<MiRespuestaRS> crearUsuario(@RequestBody UsuarioRq usuarioRq);

    /** Edita un usuario; la contraseña es opcional (404 no existe; 409 duplicado). */
    @PutMapping(value = "/{id}", produces = {"application/json"}, consumes = {"application/json"})
    ResponseEntity<MiRespuestaRS> actualizarUsuario(@PathVariable("id") Long id,
                                                    @RequestBody UsuarioRq usuarioRq);

    /** Activa o desactiva un usuario (409 si es el último administrador activo). */
    @PatchMapping(value = "/{id}/estado", produces = {"application/json"}, consumes = {"application/json"})
    ResponseEntity<MiRespuestaRS> cambiarEstadoUsuario(@PathVariable("id") Long id,
                                                       @RequestBody EstadoRq estadoRq);
}
