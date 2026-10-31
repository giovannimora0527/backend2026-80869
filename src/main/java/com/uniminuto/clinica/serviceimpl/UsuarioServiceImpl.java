package com.uniminuto.clinica.serviceimpl;

import com.uniminuto.clinica.entity.Perfil;
import com.uniminuto.clinica.entity.Usuario;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.exception.ConflictException;
import com.uniminuto.clinica.exception.NotFoundException;
import com.uniminuto.clinica.models.EstadoRq;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.models.UsuarioRS;
import com.uniminuto.clinica.models.UsuarioRq;
import com.uniminuto.clinica.repository.PerfilRepository;
import com.uniminuto.clinica.repository.UsuarioRepository;
import com.uniminuto.clinica.service.UsuarioService;
import com.uniminuto.clinica.utils.PasswordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private static final int ROL_ADMINISTRADOR = 1;
    private static final int PASSWORD_MIN = 6;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PerfilRepository perfilRepository;

    @Override
    public List<UsuarioRS> listarUsuarios() {
        Map<Integer, String> perfiles = this.perfilRepository.findAll().stream()
                .collect(Collectors.toMap(Perfil::getPerfilId, Perfil::getNombre, (a, b) -> a));
        return this.usuarioRepository.findAll().stream()
                .sorted(Comparator.comparing(Usuario::getUsername, String.CASE_INSENSITIVE_ORDER))
                .map(u -> this.aRespuesta(u, perfiles.get(u.getRol())))
                .toList();
    }

    @Override
    public UsuarioRS buscarUsuarioPorId(Long id) {
        Usuario usuario = this.obtenerUsuario(id);
        String perfil = this.perfilRepository.findById(usuario.getRol()).map(Perfil::getNombre).orElse(null);
        return this.aRespuesta(usuario, perfil);
    }

    @Override
    public MiRespuestaRS crearUsuario(UsuarioRq rq) {
        this.validar(rq, true);
        String username = rq.getUsername().trim();
        String email = rq.getEmail().trim();
        if (this.usuarioRepository.existsByUsernameIgnoreCase(username)) {
            throw new ConflictException("Ya existe un usuario con el nombre de usuario proporcionado.");
        }
        if (this.usuarioRepository.existsByEmailIgnoreCase(email)) {
            throw new ConflictException("Ya existe un usuario con el correo proporcionado.");
        }
        this.validarPerfil(rq.getRol());

        Usuario usuario = new Usuario();
        usuario.setUsername(username);
        usuario.setEmail(email);
        usuario.setRol(rq.getRol());
        usuario.setPasswordHash(PasswordUtils.generateMd5(rq.getPassword()));
        usuario.setActivo(Boolean.TRUE);
        usuario.setFechaCreacion(LocalDateTime.now());
        this.usuarioRepository.save(usuario);

        return this.respuesta(201, "Usuario creado correctamente.");
    }

    @Override
    public MiRespuestaRS actualizarUsuario(Long id, UsuarioRq rq) {
        Usuario usuario = this.obtenerUsuario(id);
        this.validar(rq, false);
        String username = rq.getUsername().trim();
        String email = rq.getEmail().trim();
        if (this.usuarioRepository.existsByUsernameIgnoreCaseAndIdNot(username, id)) {
            throw new ConflictException("Ya existe otro usuario con el nombre de usuario proporcionado.");
        }
        if (this.usuarioRepository.existsByEmailIgnoreCaseAndIdNot(email, id)) {
            throw new ConflictException("Ya existe otro usuario con el correo proporcionado.");
        }
        this.validarPerfil(rq.getRol());

        boolean dejaDeSerAdmin = usuario.getRol() == ROL_ADMINISTRADOR && rq.getRol() != ROL_ADMINISTRADOR;
        if (dejaDeSerAdmin && Boolean.TRUE.equals(usuario.getActivo()) && this.esUltimoAdminActivo(usuario)) {
            throw new ConflictException("No se puede cambiar el perfil del último administrador activo.");
        }

        usuario.setUsername(username);
        usuario.setEmail(email);
        usuario.setRol(rq.getRol());
        if (!this.esVacio(rq.getPassword())) {
            usuario.setPasswordHash(PasswordUtils.generateMd5(rq.getPassword()));
        }
        this.usuarioRepository.save(usuario);

        return this.respuesta(200, "Usuario actualizado correctamente.");
    }

    @Override
    public MiRespuestaRS cambiarEstadoUsuario(Long id, EstadoRq estadoRq) {
        if (estadoRq == null || estadoRq.getActivo() == null) {
            throw new BadRequestException("El estado del usuario es obligatorio.");
        }
        Usuario usuario = this.obtenerUsuario(id);
        if (!estadoRq.getActivo() && Boolean.TRUE.equals(usuario.getActivo())
                && usuario.getRol() == ROL_ADMINISTRADOR && this.esUltimoAdminActivo(usuario)) {
            throw new ConflictException("No se puede desactivar al último administrador activo.");
        }
        usuario.setActivo(estadoRq.getActivo());
        this.usuarioRepository.save(usuario);

        return this.respuesta(200, estadoRq.getActivo()
                ? "Usuario activado correctamente."
                : "Usuario desactivado correctamente.");
    }

    // ---------------------------------------------------------------- helpers

    private Usuario obtenerUsuario(Long id) {
        if (id == null || id <= 0) {
            throw new BadRequestException("El id del usuario es obligatorio y debe ser mayor a cero.");
        }
        return this.usuarioRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("El usuario con el id proporcionado no existe."));
    }

    private void validarPerfil(Integer rol) {
        if (!this.perfilRepository.existsById(rol)) {
            throw new BadRequestException("El perfil seleccionado no existe.");
        }
    }

    private boolean esUltimoAdminActivo(Usuario usuario) {
        return this.usuarioRepository.countByRolAndActivoTrue(ROL_ADMINISTRADOR) <= 1;
    }

    private void validar(UsuarioRq rq, boolean creando) {
        if (rq == null) {
            throw new BadRequestException("La información del usuario es obligatoria.");
        }
        if (this.esVacio(rq.getUsername())) {
            throw new BadRequestException("El nombre de usuario es obligatorio.");
        }
        if (rq.getUsername().trim().length() > 50) {
            throw new BadRequestException("El nombre de usuario no puede superar 50 caracteres.");
        }
        if (this.esVacio(rq.getEmail()) || !rq.getEmail().trim().matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            throw new BadRequestException("El correo electrónico es obligatorio y debe ser válido.");
        }
        if (rq.getRol() == null || rq.getRol() <= 0) {
            throw new BadRequestException("El perfil es obligatorio.");
        }
        if (creando && this.esVacio(rq.getPassword())) {
            throw new BadRequestException("La contraseña es obligatoria.");
        }
        if (!this.esVacio(rq.getPassword()) && rq.getPassword().length() < PASSWORD_MIN) {
            throw new BadRequestException("La contraseña debe tener al menos " + PASSWORD_MIN + " caracteres.");
        }
    }

    private UsuarioRS aRespuesta(Usuario u, String perfil) {
        UsuarioRS rs = new UsuarioRS();
        rs.setId(u.getId());
        rs.setUsername(u.getUsername());
        rs.setEmail(u.getEmail());
        rs.setRol(u.getRol());
        rs.setPerfil(perfil);
        rs.setActivo(u.getActivo());
        rs.setFechaCreacion(u.getFechaCreacion());
        return rs;
    }

    private MiRespuestaRS respuesta(int status, String mensaje) {
        MiRespuestaRS rta = new MiRespuestaRS();
        rta.setStatus(status);
        rta.setMessage(mensaje);
        return rta;
    }

    private boolean esVacio(String valor) {
        return valor == null || valor.trim().isEmpty();
    }
}
