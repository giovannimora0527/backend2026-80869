package com.uniminuto.clinica.serviceimpl;

import com.uniminuto.clinica.entity.Perfil;
import com.uniminuto.clinica.entity.Usuario;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.models.PerfilRs;
import com.uniminuto.clinica.models.UsuarioRq;
import com.uniminuto.clinica.models.UsuarioRs;
import com.uniminuto.clinica.repository.PerfilRepository;
import com.uniminuto.clinica.repository.UsuarioRepository;
import com.uniminuto.clinica.service.UsuarioService;
import com.uniminuto.clinica.util.PasswordUtil;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PerfilRepository perfilRepository;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository, PerfilRepository perfilRepository) {
        this.usuarioRepository = usuarioRepository;
        this.perfilRepository = perfilRepository;
    }

    @Override
    public List<UsuarioRs> obtenerUsuariosOrdenados() {
        List<Usuario> usuarios = usuarioRepository.findAllByOrderByUsernameAsc();
        return convertirUsuarioToUsuarioRs(usuarios);
    }

    private List<UsuarioRs> convertirUsuarioToUsuarioRs(List<Usuario> usuarios) {
        return usuarios.stream().map(usuario -> {
            UsuarioRs usuarioRs = new UsuarioRs();
            usuarioRs.setId(usuario.getId());
            usuarioRs.setUsername(usuario.getUsername());
            usuarioRs.setRol(usuario.getPerfil().getNombre());
            usuarioRs.setFechaCreacion(usuario.getFechaCreacion());
            usuarioRs.setActivo(usuario.getActivo());
            usuarioRs.setEmail(usuario.getEmail());
            return usuarioRs;
        }).toList();
    }

    @Override
    public MiRespuestaRS crearUsuario(UsuarioRq usuarioRq) {
        this.validarUsuario(usuarioRq);

        if (this.usuarioRepository.existsByUsername(usuarioRq.getUsername())) {
            throw new BadRequestException("El nombre de usuario ya existe");
        }
        if (this.usuarioRepository.existsByEmail(usuarioRq.getEmail())) {
            throw new BadRequestException("El correo electrónico ya existe");
        }

        Optional<Perfil> optPerfil = this.perfilRepository
                .findById(usuarioRq.getPerfilId());
        if (optPerfil.isEmpty()) {
            throw new BadRequestException("El perfil no existe");
        }

        String claveMD5 = PasswordUtil.generarMD5(usuarioRq.getPassword());
        Usuario usuarioNuevo = new Usuario();
        usuarioNuevo.setUsername(usuarioRq.getUsername());
        usuarioNuevo.setPasswordHash(claveMD5);
        usuarioNuevo.setPerfil(optPerfil.get());
        usuarioNuevo.setEmail(usuarioRq.getEmail());
        usuarioNuevo.setActivo(usuarioRq.getActivo() != null ? usuarioRq.getActivo() : true);
        usuarioNuevo.setFechaCreacion(LocalDateTime.now());

        usuarioRepository.save(usuarioNuevo);

        MiRespuestaRS rta = new MiRespuestaRS();
        rta.setMessage("Usuario creado exitosamente");
        rta.setStatus(200);

        return rta;
    }

    @Override
    public MiRespuestaRS actualizarUsuario(UsuarioRq usuarioRq) {
        this.validarUsuarioActualizacion(usuarioRq);

        Optional<Usuario> optUser = this.usuarioRepository.findById(usuarioRq.getId());
        if (optUser.isEmpty()) {
            throw new BadRequestException("El usuario no existe");
        }

        Usuario usuarioExistente = optUser.get();
        if (!usuarioExistente.getUsername().equals(usuarioRq.getUsername())) {
            if (this.usuarioRepository.existsByUsername(usuarioRq.getUsername())) {
                throw new BadRequestException("El nombre de usuario ya existe");
            }
        }
        usuarioExistente.setUsername(usuarioRq.getUsername());
        if (!usuarioExistente.getEmail().equals(usuarioRq.getEmail())) {
            if (this.usuarioRepository.existsByEmail(usuarioRq.getEmail())) {
                throw new BadRequestException("El correo electrónico ya existe");
            }
        }
        Optional<Perfil> optPerfil = this.perfilRepository
                .findById(usuarioRq.getPerfilId());
        if (optPerfil.isEmpty()) {
            throw new BadRequestException("El perfil no existe");
        }

        usuarioExistente.setEmail(usuarioRq.getEmail());
        usuarioExistente.setActivo(usuarioRq.getActivo());
        usuarioExistente.setPerfil(optPerfil.get());

        // La contrasena es opcional al actualizar: solo se cambia si se envia.
        if (usuarioRq.getPassword() != null && !usuarioRq.getPassword().isEmpty()) {
            usuarioExistente.setPasswordHash(PasswordUtil.generarMD5(usuarioRq.getPassword()));
        }

        usuarioRepository.save(usuarioExistente);

        MiRespuestaRS rta = new MiRespuestaRS();
        rta.setMessage("Usuario actualizado exitosamente");
        rta.setStatus(200);

        return rta;
    }

    /**
     * Validacion para la actualizacion de usuario. A diferencia de la creacion,
     * la contrasena es opcional: si no se envia, se conserva la actual.
     */
    private void validarUsuarioActualizacion(UsuarioRq usuarioRq) throws BadRequestException {
        if (usuarioRq == null) {
            throw new BadRequestException("El usuario no puede estar vacío");
        }
        if (usuarioRq.getId() == null) {
            throw new BadRequestException("El identificador del usuario es obligatorio");
        }
        if (usuarioRq.getUsername() == null || usuarioRq.getUsername().isEmpty()) {
            throw new BadRequestException("El nombre de usuario no puede estar vacío");
        }
        if (usuarioRq.getEmail() == null || usuarioRq.getEmail().isEmpty()) {
            throw new BadRequestException("El correo electrónico no puede estar vacío");
        }
        if (usuarioRq.getPerfilId() == null) {
            throw new BadRequestException("El perfil del usuario es obligatorio");
        }
    }

    @Override
    public List<PerfilRs> listarPerfiles() {
        return this.perfilRepository.findAll().stream().map(perfil -> {
            PerfilRs perfilRs = new PerfilRs();
            perfilRs.setPerfilId(perfil.getPerfilId());
            perfilRs.setNombre(perfil.getNombre());
            perfilRs.setActivo(perfil.getActivo());
            return perfilRs;
        }).toList();
    }

    private void validarUsuario(UsuarioRq usuarioRq) throws BadRequestException {
        if (usuarioRq == null) {
            throw new BadRequestException("El usuario no puede estar vacío");
        }
        if (usuarioRq.getUsername() == null || usuarioRq.getUsername().isEmpty()) {
            throw new BadRequestException("El nombre de usuario no puede estar vacío");
        }
        if (usuarioRq.getPassword() == null || usuarioRq.getPassword().isEmpty()) {
            throw new BadRequestException("La contraseña no puede estar vacía");
        }
        // El rol del usuario se determina a partir del perfil seleccionado
        // (perfilId), no del campo de texto rol.
        if (usuarioRq.getPerfilId() == null) {
            throw new BadRequestException("El rol no puede estar vacío");
        }
        if (usuarioRq.getEmail() == null || usuarioRq.getEmail().isEmpty()) {
            throw new BadRequestException("El correo electrónico no puede estar vacío");
        }
    }
}
