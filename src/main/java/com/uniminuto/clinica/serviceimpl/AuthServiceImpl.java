package com.uniminuto.clinica.serviceimpl;

import com.uniminuto.clinica.entity.Menu;
import com.uniminuto.clinica.entity.OtpToken;
import com.uniminuto.clinica.entity.Perfil;
import com.uniminuto.clinica.entity.Usuario;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.models.AuthRS;
import com.uniminuto.clinica.models.LoginRS;
import com.uniminuto.clinica.models.LoginRq;
import com.uniminuto.clinica.models.MenuRS;
import com.uniminuto.clinica.models.ReenviarOtpRq;
import com.uniminuto.clinica.models.UsuarioRS;
import com.uniminuto.clinica.models.ValidarOtpRq;
import com.uniminuto.clinica.repository.MenuRepository;
import com.uniminuto.clinica.repository.OtpTokenRepository;
import com.uniminuto.clinica.repository.PerfilRepository;
import com.uniminuto.clinica.repository.UsuarioRepository;
import com.uniminuto.clinica.security.JwtUtil;
import com.uniminuto.clinica.service.AuthService;
import com.uniminuto.clinica.service.EmailService;
import com.uniminuto.clinica.utils.PasswordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class AuthServiceImpl implements AuthService {

    private static final String MSG_CREDENCIALES = "Usuario o contraseña incorrectos";

    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private OtpTokenRepository otpTokenRepository;
    @Autowired
    private MenuRepository menuRepository;
    @Autowired
    private PerfilRepository perfilRepository;
    @Autowired
    private EmailService emailService;
    @Autowired
    private JwtUtil jwtUtil;

    @Value("${app.otp.expiracion-minutos:5}")
    private int otpExpiracionMinutos;

    @Value("${app.otp.max-intentos:3}")
    private int otpMaxIntentos;

    private final SecureRandom random = new SecureRandom();

    // ---------------------------------------------------------------- login
    @Override
    @Transactional
    public AuthRS login(LoginRq rq) {
        // 1. Validar objeto
        if (rq == null || estaVacio(rq.getUsername()) || estaVacio(rq.getPassword())) {
            throw new BadRequestException("El usuario y la contraseña son obligatorios");
        }

        // 2. Buscar por usuario (si no está, error)
        Usuario usuario = usuarioRepository.findByUsername(rq.getUsername().trim())
                .orElseThrow(() -> new BadRequestException(MSG_CREDENCIALES));

        // 3. Comparar la contraseña (hash MD5)
        String passHash = PasswordUtils.generateMd5(rq.getPassword());
        if (usuario.getPasswordHash() == null
                || !usuario.getPasswordHash().equalsIgnoreCase(passHash)) {
            throw new BadRequestException(MSG_CREDENCIALES);
        }
        if (!Boolean.TRUE.equals(usuario.getActivo())) {
            throw new BadRequestException("El usuario está inactivo. Comunícate con el administrador");
        }

        // 4. Generar token OTP y enviarlo por correo
        generarYEnviarOtp(usuario);
        return construirAuthRS(usuario);
    }

    // ---------------------------------------------------------- reenviar otp
    @Override
    @Transactional
    public AuthRS reenviarOtp(ReenviarOtpRq rq) {
        if (rq == null || rq.getUsuarioId() == null) {
            throw new BadRequestException("El usuario es obligatorio");
        }
        Usuario usuario = usuarioRepository.findById(rq.getUsuarioId())
                .orElseThrow(() -> new BadRequestException("Usuario no encontrado"));
        if (!Boolean.TRUE.equals(usuario.getActivo())) {
            throw new BadRequestException("El usuario está inactivo");
        }
        generarYEnviarOtp(usuario);
        return construirAuthRS(usuario);
    }

    // ------------------------------------------------------------ validar otp
    // noRollbackFor: para que se guarden los intentos fallidos aunque se lance el error.
    @Override
    @Transactional(noRollbackFor = BadRequestException.class)
    public LoginRS validarOtp(ValidarOtpRq rq) {
        if (rq == null || rq.getUsuarioId() == null || estaVacio(rq.getCodigo())) {
            throw new BadRequestException("El código es obligatorio");
        }
        Usuario usuario = usuarioRepository.findById(rq.getUsuarioId())
                .orElseThrow(() -> new BadRequestException("Usuario no encontrado"));

        OtpToken otp = otpTokenRepository
                .findTopByUsuarioIdAndUsadoFalseOrderByFechaCreacionDesc(usuario.getId())
                .orElseThrow(() -> new BadRequestException(
                        "No hay un código vigente. Inicia sesión de nuevo"));

        // Si está vencido, lanzo error
        if (otp.getFechaExpiracion().isBefore(LocalDateTime.now())) {
            otp.setUsado(true);
            otpTokenRepository.save(otp);
            throw new BadRequestException("El código ya venció. Solicita uno nuevo");
        }
        if (otp.getIntentos() >= otpMaxIntentos) {
            otp.setUsado(true);
            otpTokenRepository.save(otp);
            throw new BadRequestException(
                    "Superaste el número de intentos. Solicita un código nuevo");
        }

        boolean coincide = MessageDigest.isEqual(
                otp.getCodigo().getBytes(StandardCharsets.UTF_8),
                rq.getCodigo().trim().getBytes(StandardCharsets.UTF_8));
        if (!coincide) {
            otp.setIntentos(otp.getIntentos() + 1);
            otpTokenRepository.save(otp);
            int restantes = otpMaxIntentos - otp.getIntentos();
            throw new BadRequestException("El código es incorrecto. Intentos restantes: "
                    + Math.max(restantes, 0));
        }

        // OTP correcto: se marca como usado y se genera el JWT
        otp.setUsado(true);
        otpTokenRepository.save(otp);

        LoginRS rs = new LoginRS();
        rs.setToken(jwtUtil.generarToken(usuario.getId(), usuario.getUsername(), usuario.getRol()));
        rs.setExpiraEnSegundos(jwtUtil.getExpiracionSegundos());
        rs.setUsuario(construirUsuarioRS(usuario));
        rs.setMenuList(construirMenuList(usuario.getRol()));
        return rs;
    }

    // -------------------------------------------------------------- privados
    private void generarYEnviarOtp(Usuario usuario) {
        // Se invalidan los códigos anteriores sin usar
        List<OtpToken> anteriores = otpTokenRepository.findByUsuarioIdAndUsadoFalse(usuario.getId());
        for (OtpToken anterior : anteriores) {
            anterior.setUsado(true);
        }
        otpTokenRepository.saveAll(anteriores);

        String codigo = String.format("%06d", random.nextInt(1_000_000));
        LocalDateTime ahora = LocalDateTime.now();

        OtpToken otp = new OtpToken();
        otp.setUsuarioId(usuario.getId());
        otp.setCodigo(codigo);
        otp.setFechaCreacion(ahora);
        otp.setFechaExpiracion(ahora.plusMinutes(otpExpiracionMinutos));
        otp.setIntentos(0);
        otp.setUsado(false);
        otpTokenRepository.save(otp);

        emailService.enviarOtp(usuario.getEmail(), usuario.getUsername(), codigo, otpExpiracionMinutos);
    }

    private AuthRS construirAuthRS(Usuario usuario) {
        AuthRS rs = new AuthRS();
        rs.setUsuarioId(usuario.getId());
        rs.setLogin(true);
        rs.setUsername(usuario.getUsername());
        rs.setEmailEnmascarado(enmascarar(usuario.getEmail()));
        return rs;
    }

    private UsuarioRS construirUsuarioRS(Usuario usuario) {
        UsuarioRS rs = new UsuarioRS();
        rs.setId(usuario.getId());
        rs.setUsername(usuario.getUsername());
        rs.setEmail(usuario.getEmail());
        rs.setRol(usuario.getRol());
        Perfil perfil = perfilRepository.findById(usuario.getRol()).orElse(null);
        rs.setPerfil(perfil != null ? perfil.getNombre() : null);
        return rs;
    }

    private List<MenuRS> construirMenuList(Integer perfilId) {
        List<MenuRS> lista = new ArrayList<>();
        for (Menu menu : menuRepository.findByPerfilId(perfilId)) {
            MenuRS m = new MenuRS();
            m.setMenuId(menu.getMenuId());
            m.setCode(menu.getCode());
            m.setTitle(menu.getTitle());
            m.setUrl(menu.getUrl());
            m.setIcon(menu.getIcon());
            lista.add(m);
        }
        return lista;
    }

    private String enmascarar(String email) {
        if (email == null || !email.contains("@")) {
            return "";
        }
        int arroba = email.indexOf('@');
        String nombre = email.substring(0, arroba);
        String visible = nombre.length() <= 2 ? nombre.substring(0, 1) : nombre.substring(0, 2);
        return visible + "***" + email.substring(arroba);
    }

    private boolean estaVacio(String texto) {
        return texto == null || texto.trim().isEmpty();
    }
}
