package com.ms.producto_ms.service.impl;

import com.ms.producto_ms.client.ProductosClient;
import com.ms.producto_ms.dto.response.ApiResponse;
import com.ms.producto_ms.dto.response.InventarioDetalleResponse;
import com.ms.producto_ms.dto.response.ProductoResumenDto;
import com.ms.producto_ms.entity.InventarioEntity;
import com.ms.producto_ms.exception.CustomException;
import com.ms.producto_ms.repository.InventarioRepository;
import com.ms.producto_ms.service.InventarioInterface;
import com.ms.producto_ms.util.eventos.InventarioCambiadoEvent;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Map;


/**
 * Interfaz que define las operaciones de negocio relacionadas con la entidad {@link InventarioEntity}.
 * <p>
 * Esta interfaz establece el contrato para la gestión de productos en el sistema,
 * incluyendo creación, consulta, actualización y eliminación. Las implementaciones
 * concretas deben encargarse de la lógica de negocio y la interacción con el repositorio.
 *
 * @author Luis Cantillo
 * @since 1.0.0
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class InventarioService implements InventarioInterface {

    private final InventarioRepository inventarioRepository;
    private final ProductosClient productosClient;
    private final ApplicationEventPublisher eventPublisher;

    // ====== INGRESO ======
    @Override
    @Transactional
    public int agregarCantidad(Long productoId, Integer cantidad, BigDecimal precioUnitario) throws CustomException {
        if (productoId == null) {
            throw new CustomException("El ID del producto es obligatorio", HttpStatus.BAD_REQUEST);
        }
        if (cantidad == null || cantidad <= 0) {
            throw new CustomException("La cantidad a agregar debe ser mayor que 0", HttpStatus.BAD_REQUEST);
        }
        if (precioUnitario == null || precioUnitario.compareTo(BigDecimal.ZERO) <= 0) {
            throw new CustomException("El precio unitario debe ser mayor que 0", HttpStatus.BAD_REQUEST);
        }

        // Válida y trae datos básicos del producto
        ProductoResumenDto producto = obtenerProducto(productoId);

        int cantidadAnterior = inventarioRepository.obtenerSaldoPorProducto(productoId);

        BigDecimal precioTotal = precioUnitario.multiply(BigDecimal.valueOf(cantidad));

        InventarioEntity movimiento = InventarioEntity.builder()
                .productoId(productoId)
                .cantidad(cantidad)
                .tipoMovimiento("INGRESO")
                .precioUnitario(precioUnitario)
                .precioTotal(precioTotal)
                .build();

        inventarioRepository.save(movimiento);

        int cantidadNueva = cantidadAnterior + cantidad;

        log.info("Inventario INGRESO - productoId={} nombre={} cantAnterior={} mov={} cantNueva={} precioUnit={} precioTotal={}",
                productoId, producto.getNombre(), cantidadAnterior, cantidad, cantidadNueva, precioUnitario, precioTotal);


        // Emitir evento de inventario cambiado
        InventarioCambiadoEvent event = InventarioCambiadoEvent.builder()
                .productoId(productoId)
                .tipoMovimiento("INGRESO")
                .cantidadAnterior(cantidadAnterior)
                .cantidadMovimiento(cantidad)
                .cantidadNueva(cantidadNueva)
                .precioUnitario(precioUnitario)
                .build();

        eventPublisher.publishEvent(event);

        return cantidadNueva;
    }

    // ====== SALIDA ======
    @Override
    @Transactional
    public int retirarCantidad(Long productoId, Integer cantidad, BigDecimal precioUnitario) throws CustomException {
        if (productoId == null) {
            throw new CustomException("El ID del producto es obligatorio", HttpStatus.BAD_REQUEST);
        }
        if (cantidad == null || cantidad <= 0) {
            throw new CustomException("La cantidad a retirar debe ser mayor que 0", HttpStatus.BAD_REQUEST);
        }
        if (precioUnitario == null || precioUnitario.compareTo(BigDecimal.ZERO) <= 0) {
            throw new CustomException("El precio unitario debe ser mayor que 0", HttpStatus.BAD_REQUEST);
        }

        ProductoResumenDto producto = obtenerProducto(productoId);

        int cantidadAnterior = inventarioRepository.obtenerSaldoPorProducto(productoId);
        if (cantidadAnterior < cantidad) {
            throw new CustomException("No hay inventario suficiente para este producto", HttpStatus.BAD_REQUEST);
        }

        BigDecimal precioTotal = precioUnitario.multiply(BigDecimal.valueOf(cantidad));

        InventarioEntity movimiento = InventarioEntity.builder()
                .productoId(productoId)
                .cantidad(cantidad)
                .tipoMovimiento("SALIDA")
                .precioUnitario(precioUnitario)
                .precioTotal(precioTotal)
                .build();

        inventarioRepository.save(movimiento);

        int cantidadNueva = cantidadAnterior - cantidad;

        log.info("Inventario SALIDA - productoId={} nombre={} cantAnterior={} mov={} cantNueva={} precioUnit={} precioTotal={}",
                productoId, producto.getNombre(), cantidadAnterior, cantidad, cantidadNueva, precioUnitario, precioTotal);

        // Emitir evento de inventario cambiado
        InventarioCambiadoEvent event = InventarioCambiadoEvent.builder()
                .productoId(productoId)
                .tipoMovimiento("SALIDA")
                .cantidadAnterior(cantidadAnterior)
                .cantidadMovimiento(cantidad)
                .cantidadNueva(cantidadNueva)
                .precioUnitario(precioUnitario)
                .build();

        eventPublisher.publishEvent(event);

        return cantidadNueva;
    }

    // ====== CONSULTAR DETALLE (nombre, cantidad, precio unitario, total) ======
    @Override
    @Retryable(maxAttempts = 3, backoff = @Backoff(delay = 300))
    public InventarioDetalleResponse consultarDetalleInventario(Long productoId) throws CustomException {
        // 1. Info del producto (válida que exista)
        ProductoResumenDto producto = obtenerProducto(productoId);

        // 2. Saldo actual
        int saldo = inventarioRepository.obtenerSaldoPorProducto(productoId);

        // 3. Último movimiento (una sola llamada al repo)
        var ultimoMovimientoOpt = inventarioRepository
                .findTopByProductoIdOrderByFechaMovimientoDesc(productoId);

        BigDecimal precioUnitario = ultimoMovimientoOpt
                .map(InventarioEntity::getPrecioUnitario)
                .orElse(producto.getPrecio());

        // 4. Construir response
        return InventarioDetalleResponse.builder()
                .productoId(productoId)
                .nombreProducto(producto.getNombre())
                .cantidadDisponible(saldo)
                .precioUnitario(precioUnitario)
                .build();
    }


    // ====== Helper para consumir el micro de productos ======
    private ProductoResumenDto obtenerProducto(Long productoId) throws CustomException {
        try {
            ResponseEntity<ApiResponse> response = productosClient.obtenerProducto(productoId);

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                throw new CustomException("Producto no encontrado en el microservicio de productos",
                        HttpStatus.NOT_FOUND);
            }

            ApiResponse api = response.getBody();
            Object data = api.getData();
            if (!(data instanceof Map<?, ?> map)) {
                throw new CustomException("Formato inesperado en respuesta de productos",
                        HttpStatus.INTERNAL_SERVER_ERROR);
            }

            Object idObj = map.get("id");
            Object nombreObj = map.get("nombre");
            Object precioObj = map.get("precio");

            Long id = idObj != null ? Long.valueOf(idObj.toString()) : productoId;
            String nombre = nombreObj != null ? nombreObj.toString() : null;
            BigDecimal precio = precioObj != null ? new BigDecimal(precioObj.toString()) : null;

            return ProductoResumenDto.builder()
                    .id(id)
                    .nombre(nombre)
                    .precio(precio)
                    .build();

        } catch (FeignException.NotFound e) {
            // El micro de productos devolvió 404
            log.warn("Producto no encontrado en productos-ms. productoId={}", productoId);
            throw new CustomException("Producto no encontrado en el microservicio de productos",
                    HttpStatus.NOT_FOUND);

        } catch (FeignException.Forbidden e) {
            // El micro de productos devolvió 403 (normalmente API KEY mala o faltante)
            log.error("Acceso prohibido al microservicio de productos. productoId={}", productoId, e);
            throw new CustomException("Acceso prohibido al microservicio de productos",
                    HttpStatus.FORBIDDEN);

        } catch (FeignException e) {
            // Otros errores HTTP (400, 500, etc)
            log.error("Error HTTP al comunicarse con productos. status={} productoId={}",
                    e.status(), productoId, e);
            throw new CustomException("Fallo comunicación con productos",
                    HttpStatus.SERVICE_UNAVAILABLE);

        } catch (CustomException e) {
            throw e;

        } catch (Exception e) {
            log.error("Error inesperado al comunicarse con productos para productoId={}", productoId, e);
            throw new CustomException("Fallo comunicación con productos",
                    HttpStatus.SERVICE_UNAVAILABLE);
        }
    }
}
