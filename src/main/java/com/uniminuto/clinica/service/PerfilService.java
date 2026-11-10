package com.uniminuto.clinica.service;

import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.models.CambiarPasswordRq;
import com.uniminuto.clinica.models.EditarPerfilRq;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.models.UsuarioRs;

/**
 * Servicio para la gestion del perfil del usuario autenticado.
 *
 * Permite consultar y editar los datos basicos del propio usuario, asi como
 * cambiar su contrasena. Estas operaciones estan disponibles para cualquier
 * usuario autenticado, independientemente de su rol.
 */
public interface PerfilService {

    UsuarioRs obtenerPerfil(Long usuarioId) throws BadRequestException;

    MiRespuestaRS editarPerfil(EditarPerfilRq editarPerfilRq) throws BadRequestException;

    MiRespuestaRS cambiarPassword(CambiarPasswordRq cambiarPasswordRq) throws BadRequestException;
}
