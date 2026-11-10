package com.uniminuto.clinica.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuracion de la documentacion de la API mediante springdoc-openapi.
 *
 * Expone la interfaz Swagger UI y la descripcion OpenAPI de todos los
 * endpoints del sistema. Define ademas un esquema de seguridad de tipo
 * Bearer (JWT) para que desde la propia interfaz de Swagger se pueda
 * enviar el token de autenticacion en las peticiones protegidas.
 *
 * Rutas de acceso (considerando el context-path /clinica/v1):
 *   - Swagger UI:        /clinica/v1/swagger-ui.html
 *   - OpenAPI (JSON):    /clinica/v1/v3/api-docs
 */
@Configuration
public class OpenApiConfig {

    /** Nombre del esquema de seguridad declarado en la documentacion. */
    private static final String ESQUEMA_SEGURIDAD = "bearerAuth";

    @Bean
    public OpenAPI clinicaOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API Clinica Veterinaria Uniminuto")
                        .description("Servicios REST del sistema de gestion administrativa, "
                                + "medica y financiera de la clinica veterinaria.")
                        .version("v1")
                        .contact(new Contact().name("Equipo de desarrollo Full Stack")))
                .addSecurityItem(new SecurityRequirement().addList(ESQUEMA_SEGURIDAD))
                .components(new Components().addSecuritySchemes(ESQUEMA_SEGURIDAD,
                        new SecurityScheme()
                                .name(ESQUEMA_SEGURIDAD)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Token JWT obtenido al validar el OTP en /auth/validate-token. "
                                        + "Se debe enviar en la cabecera Authorization con el prefijo Bearer.")));
    }
}
