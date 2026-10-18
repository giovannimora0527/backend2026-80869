package com.uniminuto.clinica.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuración web global. Define CORS en un único lugar para que Angular pueda consumir
 * todos los endpoints (antes solo algunas interfaces tenían @CrossOrigin).
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /** Orígenes permitidos separados por coma (propiedad {@code app.cors.allowed-origins}). */
    @Value("${app.cors.allowed-origins:http://localhost:4200}")
    private String[] origenesPermitidos;

    /**
     * Registra las reglas CORS para toda la API.
     *
     * @param registry Registro de configuraciones CORS.
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(origenesPermitidos)
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*");
    }
}
