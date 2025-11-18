package com.ms.producto_ms.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

/**
 * Excepción personalizada para manejar errores específicos en la aplicación.
 * Esta clase extiende {@link RuntimeException} y permite asociar un
 * {@link HttpStatus} a la excepción, lo que facilita devolver códigos
 * de estado HTTP adecuados en controladores REST.
 * Ejemplos de uso:
 * <pre>
 *     throw new CustomException("Producto no encontrado", HttpStatus.NOT_FOUND);
 *     throw new CustomException("Error interno del servidor");
 * </pre>
 *
 * @author Luis Cantillo
 * @since 1.0.0
 */
@Setter
@Getter
public class CustomException extends Exception {
    private HttpStatus status;

    /**
     * Constructor que crea una excepción con un mensaje.
     *
     * @param message descripción del error.
     * @since 1.0.0
     */
    public CustomException(String message) {
        super(message);
        this.status = HttpStatus.BAD_REQUEST;
    }

    /**
     * Constructor que crea una excepción con un mensaje y un código de estado HTTP.
     *
     * @param message descripción del error.
     * @param status código de estado HTTP asociado.
     * @since 1.0.0
     */
    public CustomException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    /**
     * Constructor que crea una excepción a partir de otra causa.
     *
     * @param cause excepción original que provocó el error.
     * @since 1.0.0
     */
    public CustomException(Throwable cause) {
        super(cause);
        this.status = HttpStatus.BAD_REQUEST;
    }

}
