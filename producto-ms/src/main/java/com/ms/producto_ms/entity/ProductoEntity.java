package com.ms.producto_ms.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.Objects;

/**
 * Esta clase define las propiedades y comportamientos asociados
 * a un producto, incluyendo atributos como nombre, precio,
 * categoría y estado de disponibilidad.
 *
 * @author Luis Cantillo
 * @since 1.0.0
 */
@Setter
@Getter
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "productos")
public class ProductoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable=false)
    private String nombre;
    @Column(nullable=false)
    private String descripcion;
    @Column(name = "imagen_path", length = 350)
    private String imagenPath;
    @Column(nullable=false)
    private Double precio;
    @Column(nullable=false)
    private Boolean activo = true;

    @PrePersist
    public void prePersist() {
        // Valores por defecto
        if (Objects.isNull(activo)) activo = true;
    }
}
