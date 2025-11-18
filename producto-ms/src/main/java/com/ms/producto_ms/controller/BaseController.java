package com.ms.producto_ms.controller;

import com.ms.producto_ms.dto.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

/**
 * Clase base para controladores REST.
 * <p>
 * Proporciona métodos utilitarios para construir respuestas estándar
 * en formato {@link ApiResponse}, siguiendo el estilo JSON:API.
 * <p>
 * Los métodos encapsulan la creación de respuestas exitosas y personalizadas,
 * reduciendo duplicación de código en controladores concretos.
 * <p>
 * Ejemplo de uso:
 * <pre>
 *     return createSuccessResponse(producto);
 *     return createSuccessResponseList(listaProductos);
 *     return createCustomResponse(producto, "Producto creado", HttpStatus.CREATED);
 * </pre>
 *
 * @author Luis Cantillo
 * @since 1.0.0
 */
public class BaseController {

    /**
     * Crea una respuesta exitosa con un único objeto de datos.
     *
     * @param data objeto que representa el recurso o información a devolver.
     * @return {@link ResponseEntity} con un {@link ApiResponse} que incluye el objeto,
     * mensaje "success" y código HTTP 200 (OK).
     */
    protected ResponseEntity<ApiResponse> createSuccessResponse(Object data) {
        ApiResponse response = ApiResponse.builder()
                .jsonapi(Map.of("version", "1.0"))
                .data(data)
                .message("success")
                .code(HttpStatus.OK.value())
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * Crea una respuesta exitosa con una lista de objetos de datos.
     *
     * @param data lista de objetos que representan los recursos o información a devolver.
     * @return {@link ResponseEntity} con un {@link ApiResponse} que incluye la lista,
     * mensaje "success" y código HTTP 200 (OK).
     */
    protected ResponseEntity<ApiResponse> createSuccessResponseList(List<Object> data) {
        ApiResponse response = ApiResponse.builder()
                .jsonapi(Map.of("version", "1.0"))
                .data(data)
                .message("success")
                .code(HttpStatus.OK.value())
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * Crea una respuesta personalizada con datos, mensaje y código de estado.
     *
     * @param data    objeto que representa el recurso o información a devolver.
     * @param message mensaje personalizado que describe el resultado de la operación.
     * @param status  código de estado HTTP que representa el resultado.
     * @return {@link ResponseEntity} con un {@link ApiResponse} que incluye el objeto,
     * mensaje personalizado y el código de estado indicado.
     */
    protected ResponseEntity<ApiResponse> createCustomResponse(Object data, String message, HttpStatus status) {
        ApiResponse response = ApiResponse.builder()
                .jsonapi(Map.of("version", "1.0"))
                .data(data)
                .message(message)
                .code(status.value())
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
