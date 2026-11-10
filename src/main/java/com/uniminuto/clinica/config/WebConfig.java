package com.uniminuto.clinica.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuracion web global.
 *
 * Define una politica CORS unica para toda la aplicacion. Esto resulta
 * necesario para que las respuestas generadas directamente por el filtro de
 * autenticacion (por ejemplo, los errores 401 y 403) tambien incluyan las
 * cabeceras CORS y puedan ser leidas por el frontend Angular.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
                .allowedHeaders("*");
    }
}
