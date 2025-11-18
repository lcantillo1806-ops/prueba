package com.ms.producto_ms.config.pojo;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "app")
public class AppProperties {
    private SwaggerDto swagger;

    @Getter
    @Setter
    public static class SwaggerDto{
        private String title;
        private String description;
        private String version;
    }
}
