package com.uniminuto.clinica.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.IOException;
import java.util.List;

@Configuration
public class InventarioPermisosConfig implements WebMvcConfigurer {

    private final JdbcTemplate jdbc;

    public InventarioPermisosConfig(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new PermisoInventario())
                .addPathPatterns("/medicamento", "/medicamento/**");
    }

    private class PermisoInventario implements HandlerInterceptor {

        @Override
        public boolean preHandle(
                HttpServletRequest request,
                HttpServletResponse response,
                Object handler
        ) throws IOException {
            String metodo = request.getMethod();

            if ("OPTIONS".equals(metodo)) {
                return true;
            }

            String cabecera = request.getHeader("Authorization");
            if (cabecera == null || !cabecera.startsWith("Bearer ")) {
                return rechazar(response, 401, "Inicia sesión para consultar el inventario.");
            }

            String token = cabecera.substring(7).trim();
            if (token.isEmpty()) {
                return rechazar(response, 401, "La sesión no es válida.");
            }

            List<String> perfiles = jdbc.queryForList("""
                    SELECT p.nombre
                    FROM `session` s
                    JOIN usuario u ON u.id = s.user_id
                    JOIN perfil p ON p.perfil_id = u.rol
                    WHERE BINARY s.token = BINARY ?
                      AND s.fecha_expiracion > CURRENT_TIMESTAMP
                      AND u.activo = 1
                      AND p.activo = 1
                    ORDER BY s.session_id DESC
                    LIMIT 1
                    """, String.class, token);

            if (perfiles.isEmpty()) {
                return rechazar(response, 401, "La sesión venció. Vuelve a ingresar.");
            }

            boolean consulta = "GET".equals(metodo) || "HEAD".equals(metodo);
            boolean administrador = "Administrador".equalsIgnoreCase(perfiles.get(0).trim());

            if (!consulta && !administrador) {
                return rechazar(response, 403, "Tu perfil solo puede consultar el inventario.");
            }

            return true;
        }
    }

    private boolean rechazar(
            HttpServletResponse response,
            int estado,
            String mensaje
    ) throws IOException {
        response.setStatus(estado);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(
                "{\"status\":" + estado + ",\"message\":\"" + mensaje + "\"}"
        );
        return false;
    }
}