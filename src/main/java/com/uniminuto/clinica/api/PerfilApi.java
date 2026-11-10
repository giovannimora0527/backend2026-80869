package com.uniminuto.clinica.api;

import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.models.CambiarPasswordRq;
import com.uniminuto.clinica.models.EditarPerfilRq;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.models.UsuarioRs;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Endpoints para la gestion del perfil del usuario autenticado.
 *
 * A diferencia del modulo /usuario (restringido al rol Administrador), estos
 * servicios estan disponibles para cualquier usuario autenticado, ya que
 * operan unicamente sobre su propia cuenta.
 */
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/perfil")
public interface PerfilApi {

    @GetMapping(value = "/obtener",
            produces = {"application/json"})
    ResponseEntity<UsuarioRs> obtenerPerfil(
            @RequestParam("usuarioId") Long usuarioId)
            throws BadRequestException;

    @PostMapping(value = "/editar",
            produces = {"application/json"},
            consumes = {"application/json"})
    ResponseEntity<MiRespuestaRS> editarPerfil(
            @RequestBody EditarPerfilRq editarPerfilRq)
            throws BadRequestException;

    @PostMapping(value = "/cambiar-password",
            produces = {"application/json"},
            consumes = {"application/json"})
    ResponseEntity<MiRespuestaRS> cambiarPassword(
            @RequestBody CambiarPasswordRq cambiarPasswordRq)
            throws BadRequestException;
}
