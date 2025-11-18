package com.ms.producto_ms.mapper;

import com.ms.producto_ms.dto.response.ProductoResponse;
import com.ms.producto_ms.entity.InventarioEntity;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-11-18T03:14:38-0500",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.7 (Oracle Corporation)"
)
@Component
public class ProductoMapperImpl implements ProductoMapper {

    @Override
    public ProductoResponse mapToResponse(InventarioEntity entity) {
        if ( entity == null ) {
            return null;
        }

        ProductoResponse.ProductoResponseBuilder productoResponse = ProductoResponse.builder();

        productoResponse.id( entity.getId() );

        return productoResponse.build();
    }
}
