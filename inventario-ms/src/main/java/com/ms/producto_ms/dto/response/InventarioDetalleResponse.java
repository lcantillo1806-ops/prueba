package com.ms.producto_ms.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventarioDetalleResponse {

    private Long productoId;
    private String nombreProducto;
    private Integer cantidadDisponible;
    private BigDecimal precioUnitario;
}
