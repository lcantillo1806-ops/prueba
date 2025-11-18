package com.ms.producto_ms.service.impl;

import com.ms.producto_ms.client.ProductosClient;
import com.ms.producto_ms.dto.response.ApiResponse;
import com.ms.producto_ms.dto.response.InventarioDetalleResponse;
import com.ms.producto_ms.entity.InventarioEntity;
import com.ms.producto_ms.exception.CustomException;
import com.ms.producto_ms.repository.InventarioRepository;
import com.ms.producto_ms.util.eventos.InventarioCambiadoEvent;
import feign.FeignException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventarioServiceTest {

    @Mock
    private InventarioRepository inventarioRepository;

    @Mock
    private ProductosClient productosClient;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private InventarioService inventarioService;

    // ================= CAMINOS FELICES (ya los tenías) =================

    @Test
    @DisplayName("agregarCantidad - debe sumar cantidad, publicar evento y retornar nuevo saldo")
    void agregarCantidadHappyPath() throws CustomException {
        Long productoId = 1L;
        Integer cantidadMovimiento = 3;
        BigDecimal precioUnitario = new BigDecimal("1000");

        when(inventarioRepository.obtenerSaldoPorProducto(productoId)).thenReturn(5);

        Map<String, Object> data = Map.of(
                "id", productoId,
                "nombre", "Camiseta verde",
                "precio", new BigDecimal("1000")
        );

        ApiResponse apiResponse = ApiResponse.builder()
                .data(data)
                .code(HttpStatus.OK.value())
                .message("ok")
                .build();

        when(productosClient.obtenerProducto(productoId))
                .thenReturn(ResponseEntity.ok(apiResponse));

        int nuevoSaldo = inventarioService.agregarCantidad(productoId, cantidadMovimiento, precioUnitario);

        assertEquals(8, nuevoSaldo); // 5 + 3

        verify(inventarioRepository, times(1)).obtenerSaldoPorProducto(productoId);
        verify(inventarioRepository, times(1)).save(any(InventarioEntity.class));
        verify(productosClient, times(1)).obtenerProducto(productoId);


        ArgumentCaptor<InventarioCambiadoEvent> eventCaptor =
                ArgumentCaptor.forClass(InventarioCambiadoEvent.class);
        verify(eventPublisher, times(1)).publishEvent(eventCaptor.capture());

        InventarioCambiadoEvent event = eventCaptor.getValue();
        assertEquals(productoId, event.getProductoId());
        assertEquals(5, event.getCantidadAnterior());
        assertEquals(8, event.getCantidadNueva());
    }

    @Test
    @DisplayName("retirarCantidad - debe restar cantidad, publicar evento y retornar nuevo saldo")
    void retirarCantidadHappyPath() throws CustomException {
        Long productoId = 1L;
        Integer cantidadMovimiento = 4;
        BigDecimal precioUnitario = new BigDecimal("2000");

        when(inventarioRepository.obtenerSaldoPorProducto(productoId)).thenReturn(10);

        Map<String, Object> data = Map.of(
                "id", productoId,
                "nombre", "Camiseta verde",
                "precio", new BigDecimal("2000")
        );

        ApiResponse apiResponse = ApiResponse.builder()
                .data(data)
                .code(HttpStatus.OK.value())
                .message("ok")
                .build();

        when(productosClient.obtenerProducto(productoId))
                .thenReturn(ResponseEntity.ok(apiResponse));

        int nuevoSaldo = inventarioService.retirarCantidad(productoId, cantidadMovimiento, precioUnitario);

        assertEquals(6, nuevoSaldo); // 10 - 4

        verify(inventarioRepository, times(1)).obtenerSaldoPorProducto(productoId);
        verify(inventarioRepository, times(1)).save(any(InventarioEntity.class));
        verify(productosClient, times(1)).obtenerProducto(productoId);

        ArgumentCaptor<InventarioCambiadoEvent> eventCaptor =
                ArgumentCaptor.forClass(InventarioCambiadoEvent.class);
        verify(eventPublisher, times(1)).publishEvent(eventCaptor.capture());

        InventarioCambiadoEvent event = eventCaptor.getValue();
        assertEquals(productoId, event.getProductoId());
        assertEquals(10, event.getCantidadAnterior());
        assertEquals(6, event.getCantidadNueva());
    }


    @Test
    @DisplayName("consultarDetalleInventario - debe retornar nombre, cantidad y precio unitario")
    void consultarDetalleInventarioHappyPath() throws CustomException {
        Long productoId = 1L;

        when(inventarioRepository.obtenerSaldoPorProducto(productoId)).thenReturn(15);

        BigDecimal precioUltimoMovimiento = new BigDecimal("2500");
        InventarioEntity ultimoMovimiento = InventarioEntity.builder()
                .productoId(productoId)
                .cantidad(5)
                .precioUnitario(precioUltimoMovimiento)
                .build();

        when(inventarioRepository.findTopByProductoIdOrderByFechaMovimientoDesc(productoId))
                .thenReturn(Optional.of(ultimoMovimiento));

        Map<String, Object> data = Map.of(
                "id", productoId,
                "nombre", "Camiseta verde",
                "precio", new BigDecimal("2000")
        );

        ApiResponse apiResponse = ApiResponse.builder()
                .data(data)
                .code(HttpStatus.OK.value())
                .message("ok")
                .build();

        when(productosClient.obtenerProducto(productoId))
                .thenReturn(ResponseEntity.ok(apiResponse));

        InventarioDetalleResponse detalle = inventarioService.consultarDetalleInventario(productoId);

        assertNotNull(detalle);
        assertEquals(productoId, detalle.getProductoId());
        assertEquals("Camiseta verde", detalle.getNombreProducto());
        assertEquals(15, detalle.getCantidadDisponible());
        assertEquals(0, precioUltimoMovimiento.compareTo(detalle.getPrecioUnitario()));

        verify(inventarioRepository, times(1)).obtenerSaldoPorProducto(productoId);
        verify(inventarioRepository, times(1)).findTopByProductoIdOrderByFechaMovimientoDesc(productoId);
        verify(productosClient, times(1)).obtenerProducto(productoId);

        verifyNoInteractions(eventPublisher);
    }

    // ================= CASOS DE ERROR DE obtenerProducto =================

    @Test
    @DisplayName("obtenerProducto - 404 en productos-ms lanza CustomException NOT_FOUND")
    void obtenerProducto_NotFound() {
        Long productoId = 99L;

        FeignException notFound = mock(FeignException.NotFound.class);
        when(productosClient.obtenerProducto(productoId)).thenThrow(notFound);

        CustomException ex = assertThrows(
                CustomException.class,
                () -> inventarioService.consultarDetalleInventario(productoId)
        );

        assertTrue(ex.getMessage().contains("Producto no encontrado"));
    }

    @Test
    @DisplayName("obtenerProducto - 403 en productos-ms lanza CustomException FORBIDDEN")
    void obtenerProducto_Forbidden() {
        Long productoId = 99L;

        FeignException forbidden = mock(FeignException.Forbidden.class);
        when(productosClient.obtenerProducto(productoId)).thenThrow(forbidden);

        CustomException ex = assertThrows(
                CustomException.class,
                () -> inventarioService.consultarDetalleInventario(productoId)
        );

        assertTrue(ex.getMessage().contains("Acceso prohibido"));
    }

    @Test
    @DisplayName("obtenerProducto - otros errores Feign lanzan CustomException SERVICE_UNAVAILABLE")
    void obtenerProducto_OtroErrorFeign() {
        Long productoId = 99L;

        FeignException generic = mock(FeignException.class);
        when(productosClient.obtenerProducto(productoId)).thenThrow(generic);

        CustomException ex = assertThrows(
                CustomException.class,
                () -> inventarioService.consultarDetalleInventario(productoId)
        );

        assertTrue(ex.getMessage().contains("Fallo comunicación con productos"));
    }

    @Test
    @DisplayName("obtenerProducto - respuesta no 2xx o sin body lanza CustomException NOT_FOUND")
    void obtenerProducto_No2xx_O_BodyNull() {
        Long productoId = 99L;

        ApiResponse bodyNull = null;
        when(productosClient.obtenerProducto(productoId))
                .thenReturn(ResponseEntity.status(HttpStatus.NOT_FOUND).body(bodyNull));

        CustomException ex = assertThrows(
                CustomException.class,
                () -> inventarioService.consultarDetalleInventario(productoId)
        );

        assertTrue(ex.getMessage().contains("Producto no encontrado"));
    }

    @Test
    @DisplayName("obtenerProducto - formato inesperado en data lanza CustomException INTERNAL_SERVER_ERROR")
    void obtenerProducto_FormatoInesperado() {
        Long productoId = 1L;

        ApiResponse apiResponse = ApiResponse.builder()
                .data("esto no es un mapa")
                .code(HttpStatus.OK.value())
                .message("ok")
                .build();

        when(productosClient.obtenerProducto(productoId))
                .thenReturn(ResponseEntity.ok(apiResponse));

        CustomException ex = assertThrows(
                CustomException.class,
                () -> inventarioService.consultarDetalleInventario(productoId)
        );

        assertTrue(ex.getMessage().contains("Formato inesperado"));
    }

    @Test
    @DisplayName("obtenerProducto - excepción genérica lanza CustomException SERVICE_UNAVAILABLE")
    void obtenerProducto_ErrorGenerico() {
        Long productoId = 1L;

        when(productosClient.obtenerProducto(productoId))
                .thenThrow(new RuntimeException("Error raro"));

        CustomException ex = assertThrows(
                CustomException.class,
                () -> inventarioService.consultarDetalleInventario(productoId)
        );

        assertTrue(ex.getMessage().contains("Fallo comunicación con productos"));
    }
}