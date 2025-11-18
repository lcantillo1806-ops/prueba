package com.ms.producto_ms.config;



import feign.RequestInterceptor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.retry.annotation.EnableRetry;

@Configuration
public class FeignConfig {

    @Value("${security.outbound-api-key:super-secret-key}")
    private String outboundApiKey;

    @Bean
    public RequestInterceptor apiKeyRequestInterceptor() {
        return template -> {
            template.header("X-API-KEY", outboundApiKey);
        };
    }
}