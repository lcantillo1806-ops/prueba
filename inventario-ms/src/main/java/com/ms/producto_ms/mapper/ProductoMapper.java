package com.ms.producto_ms.mapper;

import com.ms.producto_ms.dto.response.ProductoResponse;
import com.ms.producto_ms.entity.InventarioEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductoMapper {
    ProductoResponse mapToResponse(InventarioEntity entity);
}
