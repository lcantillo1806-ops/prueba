package com.ms.producto_ms.controller;

import com.ms.producto_ms.dto.request.MovimientoInventarioRequest;
import com.ms.producto_ms.dto.response.ApiResponse;
import com.ms.producto_ms.dto.response.InventarioDetalleResponse;
import com.ms.producto_ms.exception.CustomException;
import com.ms.producto_ms.service.InventarioInterface;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventarioControllerTest {

    @Mock
    private InventarioInterface inventarioInterface;

    @InjectMocks
    private InventarioController inventarioController;

    @Test
    @DisplayName("getDetalle - debe retornar detalle de inventario correctamente")
    void getDetalleSuccess() throws CustomException {
        // Arrange
        Long productoId = 1L;

        InventarioDetalleResponse detalle = InventarioDetalleResponse.builder()
                .productoId(productoId)
                .nombreProducto("Camiseta verde")
                .cantidadDisponible(10)
                .precioUnitario(new BigDecimal("15000"))
                .build();

        when(inventarioInterface.consultarDetalleInventario(productoId))
                .thenReturn(detalle);

        // Act
        ResponseEntity<ApiResponse> response = inventarioController.getDetalle(productoId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        ApiResponse body = response.getBody();
        assertNotNull(body);
        assertEquals("Detalle de inventario consultado correctamente.", body.getMessage());
        assertEquals(HttpStatus.OK.value(), body.getCode());
        assertNotNull(body.getJsonapi());
        assertEquals("1.0", body.getJsonapi().get("version"));

        assertTrue(body.getData() instanceof InventarioDetalleResponse);
        InventarioDetalleResponse data = (InventarioDetalleResponse) body.getData();
        assertEquals(productoId, data.getProductoId());
        assertEquals("Camiseta verde", data.getNombreProducto());
        assertEquals(10, data.getCantidadDisponible());

        verify(inventarioInterface, times(1)).consultarDetalleInventario(productoId);
    }

    @Test
    @DisplayName("getDetalle - debe propagar CustomException cuando el servicio falla")
    void getDetalleThrowsCustomException() throws CustomException {
        // Arrange
        Long productoId = 99L;
        when(inventarioInterface.consultarDetalleInventario(productoId))
                .thenThrow(new CustomException("Error consultando detalle"));

        // Act & Assert
        CustomException ex = assertThrows(
                CustomException.class,
                () -> inventarioController.getDetalle(productoId)
        );

        assertTrue(ex.getMessage().contains("Error consultando detalle"));
        verify(inventarioInterface, times(1)).consultarDetalleInventario(productoId);
    }

    @Test
    @DisplayName("ingresar - debe registrar ingreso y retornar nueva cantidad")
    void ingresarSuccess() throws CustomException {
        // Arrange
        Long productoId = 1L;
        MovimientoInventarioRequest request = MovimientoInventarioRequest.builder()
                .cantidad(5)
                .precioUnitario(new BigDecimal("10000"))
                .build();

        when(inventarioInterface.agregarCantidad(productoId, request.getCantidad(), request.getPrecioUnitario()))
                .thenReturn(15); // nueva cantidad

        // Act
        ResponseEntity<ApiResponse> response = inventarioController.ingresar(productoId, request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        ApiResponse body = response.getBody();
        assertNotNull(body);
        assertEquals("Ingreso registrado correctamente.", body.getMessage());
        assertEquals(HttpStatus.OK.value(), body.getCode());
        assertNotNull(body.getJsonapi());
        assertEquals("1.0", body.getJsonapi().get("version"));

        assertTrue(body.getData() instanceof Map);
        Map<?, ?> data = (Map<?, ?>) body.getData();
        assertEquals(productoId, data.get("productoId"));
        assertEquals(15, data.get("cantidadNueva"));

        verify(inventarioInterface, times(1))
                .agregarCantidad(productoId, request.getCantidad(), request.getPrecioUnitario());
    }

    @Test
    @DisplayName("retirar - debe registrar salida y retornar nueva cantidad")
    void retirarSuccess() throws CustomException {
        // Arrange
        Long productoId = 1L;
        MovimientoInventarioRequest request = MovimientoInventarioRequest.builder()
                .cantidad(3)
                .precioUnitario(new BigDecimal("10000"))
                .build();

        when(inventarioInterface.retirarCantidad(productoId, request.getCantidad(), request.getPrecioUnitario()))
                .thenReturn(7); // nueva cantidad

        // Act
        ResponseEntity<ApiResponse> response = inventarioController.retirar(productoId, request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        ApiResponse body = response.getBody();
        assertNotNull(body);
        assertEquals("Salida registrada correctamente.", body.getMessage());
        assertEquals(HttpStatus.OK.value(), body.getCode());
        assertNotNull(body.getJsonapi());
        assertEquals("1.0", body.getJsonapi().get("version"));

        assertTrue(body.getData() instanceof Map);
        Map<?, ?> data = (Map<?, ?>) body.getData();
        assertEquals(productoId, data.get("productoId"));
        assertEquals(7, data.get("cantidadNueva"));

        verify(inventarioInterface, times(1))
                .retirarCantidad(productoId, request.getCantidad(), request.getPrecioUnitario());
    }
}
