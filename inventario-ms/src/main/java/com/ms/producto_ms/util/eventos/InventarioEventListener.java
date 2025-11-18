package com.ms.producto_ms.util.eventos;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class InventarioEventListener {

    @EventListener
    public void onInventarioCambiado(InventarioCambiadoEvent event) {
        log.info(
                "💡 [EVENTO] Inventario cambiado - productoId={}, tipoMovimiento={}, cantidadAnterior={}, cantidadMovimiento={}, cantidadNueva={}, precioUnitario={}",
                event.getProductoId(),
                event.getTipoMovimiento(),
                event.getCantidadAnterior(),
                event.getCantidadMovimiento(),
                event.getCantidadNueva(),
                event.getPrecioUnitario()
        );
    }
}