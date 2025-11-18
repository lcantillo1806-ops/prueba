package com.ms.producto_ms.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

/**
 * Esta clase define las propiedades y comportamientos asociados
 * a un producto, incluyendo atributos como nombre, precio,
 * categoría y estado de disponibilidad.
 *
 * @author Luis Cantillo
 * @since 1.0.0
 */
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductoResponse {
    @Schema(description = "Identificador único del producto", example = "101")
    private Long id;

    @Schema(description = "Nombre del producto", example = "Laptop Lenovo ThinkPad")
    private String nombre;

    @Schema(description = "Descripción detallada del producto", example = "Laptop de 14 pulgadas con procesador Intel i7")
    private String descripcion;

    @Schema(description = "Imagen del producto", example = "base64")
    private String imagenBase64;

    @Schema(description = "Precio del producto en dólares", example = "1200.50")
    private Double precio;

    @Schema(description = "Indica si el producto está activo o disponible", example = "true", defaultValue = "true")
    private Boolean activo;
}
