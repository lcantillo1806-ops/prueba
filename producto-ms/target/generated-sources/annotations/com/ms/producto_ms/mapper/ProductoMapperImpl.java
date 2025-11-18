package com.ms.producto_ms.mapper;

import com.ms.producto_ms.dto.response.ProductoResponse;
import com.ms.producto_ms.entity.ProductoEntity;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-11-17T19:33:22-0500",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.9 (Microsoft)"
)
@Component
public class ProductoMapperImpl implements ProductoMapper {

    @Override
    public ProductoResponse mapToResponse(ProductoEntity entity) {
        if ( entity == null ) {
            return null;
        }

        ProductoResponse.ProductoResponseBuilder productoResponse = ProductoResponse.builder();

        productoResponse.id( entity.getId() );
        productoResponse.nombre( entity.getNombre() );
        productoResponse.descripcion( entity.getDescripcion() );
        productoResponse.precio( entity.getPrecio() );
        productoResponse.activo( entity.getActivo() );

        return productoResponse.build();
    }
}
