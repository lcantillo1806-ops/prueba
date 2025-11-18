package com.ms.producto_ms.util.eventos;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder
public class InventarioCambiadoEvent {

    Long productoId;
    Integer cantidadAnterior;
    Integer cantidadMovimiento;
    Integer cantidadNueva;
    String tipoMovimiento; // "INGRESO" | "SALIDA"
    BigDecimal precioUnitario;
}