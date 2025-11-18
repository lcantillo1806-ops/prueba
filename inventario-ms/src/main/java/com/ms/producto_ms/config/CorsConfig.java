package com.ms.producto_ms.config;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Component
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*") // Permitir acceso desde este origen
                .allowedOrigins("http://localhost:4300")
                .allowedMethods("GET", "POST", "PUT", "DELETE","OPTIONS") // Métodos permitidos
                .allowedHeaders("*") // Todos los encabezados permitidos
                .allowedHeaders("Content-Type") // Todos los encabezados permitidos
                .allowedHeaders("Authorization") // Todos los encabezados permitidos
                .allowCredentials(false) // Permitir credenciales (si es necesario)
                .maxAge(3600); // cachea la respuesta OPTIONS por 1 hora

    }
}
