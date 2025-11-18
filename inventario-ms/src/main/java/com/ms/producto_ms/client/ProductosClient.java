package com.ms.producto_ms.client;

import com.ms.producto_ms.config.FeignConfig;
import com.ms.producto_ms.dto.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "productosClient",
        url = "${clients.productos.base-url}",
        configuration = FeignConfig.class
)
public interface ProductosClient {

    /**
     * Consulta un producto específico en el servicio remoto de productos.
     *
     * @param id identificador único del producto (ejemplo: 1).
     * @return ResponseEntity con ApiResponse del micro de productos.
     */
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<ApiResponse> obtenerProducto(@PathVariable("id") Long id);
}