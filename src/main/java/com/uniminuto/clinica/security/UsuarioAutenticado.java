package com.uniminuto.clinica.security;

/**
 * Representa la informacion del usuario autenticado que se extrae del JWT
 * durante el procesamiento de una peticion.
 *
 * Se expone a traves de {@link ContextoSeguridad} para que los servicios
 * puedan conocer quien realiza la operacion (por ejemplo, para registrar el
 * usuario responsable en los movimientos de inventario o despacho) sin tener
 * que volver a leer ni validar el token.
 *
 * @param usuarioId Identificador del usuario (claim "sub" del token).
 * @param username  Nombre de usuario (claim "username").
 * @param email     Correo electronico (claim "email").
 * @param rol       Nombre del perfil o rol (claim "rol").
 */
public record UsuarioAutenticado(Long usuarioId, String username, String email, String rol) {
}
