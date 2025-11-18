package com.ms.producto_ms.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

        // Nombre del esquema de seguridad (ID interno de Swagger)
        public static final String API_KEY_SCHEME_NAME = "ApiKeyAuth";

        @Bean
        public OpenAPI customOpenAPI() {
                return new OpenAPI()
                        // Indicamos que TODAS las operaciones usan este esquema por defecto
                        .addSecurityItem(new SecurityRequirement().addList(API_KEY_SCHEME_NAME))
                        .components(new Components()
                                .addSecuritySchemes(API_KEY_SCHEME_NAME,
                                        new SecurityScheme()
                                                .type(SecurityScheme.Type.APIKEY)
                                                .in(SecurityScheme.In.HEADER)
                                                .name("X-API-KEY") // <- nombre del HEADER que mira tu filtro
                                )
                        )
                        .info(new Info()
                                .title("Productos MS")
                                .description("API del microservicio de productos protegida con API Key en el header X-API-KEY.")
                                .version("1.0.0"));
        }
}