package com.uniminuto.clinica.serviceimpl;

import com.uniminuto.clinica.entity.Usuario;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.models.CambiarPasswordRq;
import com.uniminuto.clinica.models.EditarPerfilRq;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.models.UsuarioRs;
import com.uniminuto.clinica.repository.UsuarioRepository;
import com.uniminuto.clinica.security.ContextoSeguridad;
import com.uniminuto.clinica.security.UsuarioAutenticado;
import com.uniminuto.clinica.service.PerfilService;
import com.uniminuto.clinica.util.PasswordUtil;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Implementacion del servicio de perfil del usuario autenticado.
 *
 * Todas las operaciones validan que el usuario solo pueda actuar sobre su
 * propio perfil, comparando el identificador recibido con el usuario
 * autenticado publicado en {@link ContextoSeguridad}.
 */
@Service
public class PerfilServiceImpl implements PerfilService {

    private static final int LONGITUD_MINIMA_PASSWORD = 6;

    private final UsuarioRepository usuarioRepository;

    public PerfilServiceImpl(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UsuarioRs obtenerPerfil(Long usuarioId) throws BadRequestException {
        Usuario usuario = this.obtenerUsuarioPropio(usuarioId);
        UsuarioRs usuarioRs = new UsuarioRs();
        usuarioRs.setId(usuario.getId());
        usuarioRs.setUsername(usuario.getUsername());
        usuarioRs.setEmail(usuario.getEmail());
        usuarioRs.setRol(usuario.getPerfil().getNombre());
        usuarioRs.setActivo(usuario.getActivo());
        usuarioRs.setFechaCreacion(usuario.getFechaCreacion());
        return usuarioRs;
    }

    @Override
    public MiRespuestaRS editarPerfil(EditarPerfilRq editarPerfilRq) throws BadRequestException {
        if (editarPerfilRq == null) {
            throw new BadRequestException("Los datos del perfil son obligatorios.");
        }
        if (editarPerfilRq.getUsername() == null || editarPerfilRq.getUsername().isBlank()) {
            throw new BadRequestException("El nombre de usuario no puede estar vacio.");
        }
        if (editarPerfilRq.getEmail() == null || editarPerfilRq.getEmail().isBlank()) {
            throw new BadRequestException("El correo electronico no puede estar vacio.");
        }

        Usuario usuario = this.obtenerUsuarioPropio(editarPerfilRq.getUsuarioId());

        String nuevoUsername = editarPerfilRq.getUsername().trim();
        if (!usuario.getUsername().equals(nuevoUsername)
                && this.usuarioRepository.existsByUsername(nuevoUsername)) {
            throw new BadRequestException("El nombre de usuario ya existe.");
        }

        String nuevoEmail = editarPerfilRq.getEmail().trim();
        if (!usuario.getEmail().equals(nuevoEmail)
                && this.usuarioRepository.existsByEmail(nuevoEmail)) {
            throw new BadRequestException("El correo electronico ya existe.");
        }

        usuario.setUsername(nuevoUsername);
        usuario.setEmail(nuevoEmail);
        this.usuarioRepository.save(usuario);

        MiRespuestaRS rta = new MiRespuestaRS();
        rta.setStatus(200);
        rta.setMessage("Perfil actualizado correctamente.");
        return rta;
    }

    @Override
    public MiRespuestaRS cambiarPassword(CambiarPasswordRq cambiarPasswordRq)
            throws BadRequestException {
        if (cambiarPasswordRq == null) {
            throw new BadRequestException("Los datos para el cambio de contrasena son obligatorios.");
        }
        if (cambiarPasswordRq.getPasswordActual() == null
                || cambiarPasswordRq.getPasswordActual().isBlank()) {
            throw new BadRequestException("La contrasena actual es obligatoria.");
        }
        if (cambiarPasswordRq.getPasswordNueva() == null
                || cambiarPasswordRq.getPasswordNueva().isBlank()) {
            throw new BadRequestException("La nueva contrasena es obligatoria.");
        }
        if (cambiarPasswordRq.getPasswordNueva().length() < LONGITUD_MINIMA_PASSWORD) {
            throw new BadRequestException(
                    "La nueva contrasena debe tener al menos "
                            + LONGITUD_MINIMA_PASSWORD + " caracteres.");
        }

        Usuario usuario = this.obtenerUsuarioPropio(cambiarPasswordRq.getUsuarioId());

        String passwordActualMD5 = PasswordUtil.generarMD5(cambiarPasswordRq.getPasswordActual());
        if (!usuario.getPasswordHash().equals(passwordActualMD5)) {
            throw new BadRequestException("La contrasena actual es incorrecta.");
        }

        String passwordNuevaMD5 = PasswordUtil.generarMD5(cambiarPasswordRq.getPasswordNueva());
        if (usuario.getPasswordHash().equals(passwordNuevaMD5)) {
            throw new BadRequestException(
                    "La nueva contrasena debe ser diferente de la contrasena actual.");
        }

        usuario.setPasswordHash(passwordNuevaMD5);
        this.usuarioRepository.save(usuario);

        MiRespuestaRS rta = new MiRespuestaRS();
        rta.setStatus(200);
        rta.setMessage("Contrasena actualizada correctamente.");
        return rta;
    }

    /**
     * Obtiene el usuario indicado validando que coincida con el usuario
     * autenticado. De esta forma se garantiza que un usuario solo pueda
     * consultar o modificar su propio perfil.
     */
    private Usuario obtenerUsuarioPropio(Long usuarioId) throws BadRequestException {
        if (usuarioId == null) {
            throw new BadRequestException("El identificador del usuario es obligatorio.");
        }
        UsuarioAutenticado autenticado = ContextoSeguridad.obtenerUsuario();
        if (autenticado != null
                && autenticado.usuarioId() != null
                && !autenticado.usuarioId().equals(usuarioId)) {
            throw new BadRequestException("Solo puede gestionar su propio perfil.");
        }
        Optional<Usuario> optUsuario = this.usuarioRepository.findById(usuarioId);
        if (optUsuario.isEmpty()) {
            throw new BadRequestException("El usuario no existe.");
        }
        return optUsuario.get();
    }
}
