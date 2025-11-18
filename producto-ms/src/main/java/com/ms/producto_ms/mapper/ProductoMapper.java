package com.ms.producto_ms.mapper;

import com.ms.producto_ms.dto.response.ProductoResponse;
import com.ms.producto_ms.entity.ProductoEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductoMapper {
    ProductoResponse mapToResponse(ProductoEntity entity);
}
