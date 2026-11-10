package com.uniminuto.clinica.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uniminuto.clinica.exception.dto.ErrorResponse;
import com.uniminuto.clinica.util.Utilidades;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Filtro que valida el token JWT en cada peticion HTTP y aplica el control de
 * acceso por rol en el backend.
 *
 * Responsabilidades:
 *   1. Dejar pasar sin validacion las rutas publicas (autenticacion,
 *      documentacion de la API y peticiones de verificacion CORS).
 *   2. Para el resto de rutas, exigir una cabecera Authorization con un token
 *      Bearer valido (firma correcta y no expirado).
 *   3. Extraer el rol del token y verificar, cuando corresponda, que el rol
 *      tenga permiso para la ruta solicitada.
 *   4. Publicar el usuario autenticado en {@link ContextoSeguridad} para que
 *      los servicios conozcan al responsable de la operacion.
 *
 * Se implementa como un filtro propio (sin Spring Security) para mantener la
 * configuracion simple y alineada con la arquitectura existente del proyecto.
 */
@Component
public class JwtAutenticacionFilter extends OncePerRequestFilter {

    private static final String PREFIJO_BEARER = "Bearer ";

    /**
     * Rutas publicas: no requieren token. Se comparan contra la ruta sin el
     * context-path (por ejemplo "/auth/login").
     */
    private static final List<String> RUTAS_PUBLICAS = List.of(
            "/auth/",
            "/swagger-ui",
            "/v3/api-docs",
            "/swagger-resources",
            "/webjars/"
    );

    /**
     * Reglas de acceso por rol para prefijos de ruta especificos.
     *
     * La clave es el prefijo de la ruta (sin context-path) y el valor es la
     * lista de roles autorizados. Las rutas que no aparezcan aqui solo
     * requieren un token valido (cualquier usuario autenticado).
     *
     * Este mapa se ira ampliando en las siguientes fases a medida que se
     * agreguen modulos con restricciones de rol (por ejemplo, el modulo de
     * despacho para el perfil DESPACHO_MEDICO).
     */
    private static final Map<String, List<String>> REGLAS_ACCESO_POR_ROL = Map.of(
            "/usuario", List.of("Administrador"),
            "/rol", List.of("Administrador")
    );

    @Value("${JWT_SECRET}")
    private String jwtSecret;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // Las peticiones de verificacion CORS (preflight) no llevan token.
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        String ruta = obtenerRutaRelativa(request);

        if (esRutaPublica(ruta)) {
            filterChain.doFilter(request, response);
            return;
        }

        String cabecera = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (cabecera == null || !cabecera.startsWith(PREFIJO_BEARER)) {
            escribirError(response, HttpStatus.UNAUTHORIZED,
                    "Debe iniciar sesion para acceder a este recurso.");
            return;
        }

        String token = cabecera.substring(PREFIJO_BEARER.length()).trim();
        Claims claims;
        try {
            claims = Utilidades.validarJwtToken(token, jwtSecret);
        } catch (JwtException | IllegalArgumentException | IllegalStateException ex) {
            escribirError(response, HttpStatus.UNAUTHORIZED,
                    "Su sesion no es valida o ha expirado. Por favor inicie sesion nuevamente.");
            return;
        }

        String rol = claims.get("rol", String.class);

        if (!rolTienePermiso(ruta, rol)) {
            escribirError(response, HttpStatus.FORBIDDEN,
                    "No tiene permisos para acceder a este recurso.");
            return;
        }

        UsuarioAutenticado usuario = construirUsuario(claims, rol);
        try {
            ContextoSeguridad.establecerUsuario(usuario);
            filterChain.doFilter(request, response);
        } finally {
            ContextoSeguridad.limpiar();
        }
    }

    /**
     * Obtiene la ruta de la peticion sin el context-path, de modo que las
     * comparaciones sean independientes del prefijo /clinica/v1.
     */
    private String obtenerRutaRelativa(HttpServletRequest request) {
        String ruta = request.getRequestURI();
        String contextPath = request.getContextPath();
        if (contextPath != null && !contextPath.isEmpty() && ruta.startsWith(contextPath)) {
            ruta = ruta.substring(contextPath.length());
        }
        return ruta;
    }

    private boolean esRutaPublica(String ruta) {
        return RUTAS_PUBLICAS.stream().anyMatch(ruta::startsWith);
    }

    /**
     * Verifica si el rol tiene permiso para la ruta segun las reglas de acceso.
     * Si la ruta no tiene una regla especifica, basta con estar autenticado.
     */
    private boolean rolTienePermiso(String ruta, String rol) {
        for (Map.Entry<String, List<String>> regla : REGLAS_ACCESO_POR_ROL.entrySet()) {
            if (ruta.startsWith(regla.getKey())) {
                return rol != null && regla.getValue().contains(rol);
            }
        }
        return true;
    }

    private UsuarioAutenticado construirUsuario(Claims claims, String rol) {
        Long usuarioId = null;
        if (claims.getSubject() != null) {
            try {
                usuarioId = Long.valueOf(claims.getSubject());
            } catch (NumberFormatException ignored) {
                usuarioId = null;
            }
        }
        String username = claims.get("username", String.class);
        String email = claims.get("email", String.class);
        return new UsuarioAutenticado(usuarioId, username, email, rol);
    }

    private void escribirError(HttpServletResponse response, HttpStatus estado, String mensaje)
            throws IOException {
        response.setStatus(estado.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        ErrorResponse error = new ErrorResponse(estado.value(), estado.getReasonPhrase(), mensaje);
        response.getWriter().write(objectMapper.writeValueAsString(error));
    }
}
