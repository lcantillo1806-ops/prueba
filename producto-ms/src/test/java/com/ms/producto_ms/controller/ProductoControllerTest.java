package com.ms.producto_ms.controller;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.ms.producto_ms.dto.request.ProductoRequest;
import com.ms.producto_ms.dto.response.ApiResponse;
import com.ms.producto_ms.dto.response.ProductoResponse;
import com.ms.producto_ms.enmun.MensajeEmun;
import com.ms.producto_ms.entity.ProductoEntity;
import com.ms.producto_ms.exception.CustomException;
import com.ms.producto_ms.mapper.ProductoMapper;
import com.ms.producto_ms.service.ProductoInterface;
import com.ms.producto_ms.util.UtilService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class ProductoControllerTest {

    @Mock
    private ProductoInterface productoInterface;

    @Mock
    private ProductoMapper mapper;

    @Mock
    private UtilService utilService;

    @InjectMocks
    private ProductoController productoController;

    @Test
    @DisplayName("Debe retornar ResponseEntity con mensaje de éxito cuando el producto se guarda correctamente")
    void saveWhenRequestIsValid() throws CustomException {
        // Arrange
        ProductoRequest request = ProductoRequest.builder()
                .nombre("Laptop")
                .descripcion("Gaming laptop")
                .precio(1500.0)
                .build();

        when(productoInterface.save(request)).thenReturn(new ProductoEntity());

        // Act
        ResponseEntity<ApiResponse> response = productoController.save(request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        verify(productoInterface, times(1)).save(request);
    }

    @Test
    @DisplayName("Debe lanzar CustomException cuando el servicio falla al guardar el producto")
    void saveWhenServiceFails() throws CustomException {
        // Arrange
        ProductoRequest request = ProductoRequest.builder()
                .nombre("Phone")
                .descripcion("Smartphone")
                .precio(800.0)
                .build();

        doThrow(new CustomException("Error al guardar")).when(productoInterface).save(request);

        // Act & Assert
        CustomException exception = assertThrows(CustomException.class, () -> productoController.save(request));
        assertTrue(exception.getMessage().contains("Error al guardar"));

        verify(productoInterface, times(1)).save(request);
    }

    @Test
    @DisplayName("Debe retornar ResponseEntity con productos cuando existen datos")
    void getAllSuccessResponseWhenDataExists() throws CustomException {
        // Arrange
        int page = 0;
        int size = 10;
        String sortDirection = "asc";
        String sortBy = "id";

        ProductoEntity entity = ProductoEntity.builder()
                .id(1L)
                .nombre("Laptop")
                .descripcion("Gaming laptop")
                .precio(1500.0)
                .imagenPath("images/laptop.png")
                .build();

        ProductoResponse responseMapped = ProductoResponse.builder()
                .id(1L)
                .nombre("Laptop")
                .descripcion("Gaming laptop")
                .precio(1500.0)
                .build();

        Page<ProductoEntity> pageEntities = new PageImpl<>(List.of(entity));

        when(productoInterface.getAll(page, size, sortDirection, sortBy)).thenReturn(pageEntities);
        when(mapper.mapToResponse(entity)).thenReturn(responseMapped);
        when(utilService.getImagenBase64("images/laptop.png")).thenReturn("base64string");

        // Act
        ResponseEntity<ApiResponse> response = productoController.getAll(page, size, sortDirection, sortBy);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        ApiResponse apiResponse = response.getBody();
        assertNotNull(apiResponse);

        Page<?> pageResult = (Page<?>) apiResponse.getData();
        ProductoResponse productoResponse = (ProductoResponse) pageResult.getContent().get(0);
        assertEquals("Laptop", productoResponse.getNombre());
        assertEquals("base64string", productoResponse.getImagenBase64());

        verify(productoInterface, times(1)).getAll(page, size, sortDirection, sortBy);
        verify(mapper, times(1)).mapToResponse(entity);
        verify(utilService, times(1)).getImagenBase64("images/laptop.png");
    }

    @Test
    @DisplayName("Debe lanzar CustomException cuando el servicio falla en getAll")
    void getAllWhenServiceFails() throws CustomException {
        // Arrange
        int page = 0;
        int size = 10;
        String sortDirection = "asc";
        String sortBy = "id";

        when(productoInterface.getAll(page, size, sortDirection, sortBy))
                .thenThrow(new CustomException("Error al obtener productos"));

        // Act & Assert
        CustomException exception = assertThrows(
                CustomException.class,
                () -> productoController.getAll(page, size, sortDirection, sortBy)
        );
        assertTrue(exception.getMessage().contains("Error al obtener productos"));

        verify(productoInterface, times(1)).getAll(page, size, sortDirection, sortBy);
    }

    @Test
    @DisplayName("Debe setear imagenBase64 en null cuando utilService falla")
    void getAllWhenUtilServiceFails() throws CustomException {
        // Arrange
        int page = 0;
        int size = 10;
        String sortDirection = "asc";
        String sortBy = "id";

        ProductoEntity entity = ProductoEntity.builder()
                .id(2L)
                .nombre("Phone")
                .descripcion("Smartphone")
                .precio(800.0)
                .imagenPath("images/phone.png")
                .build();

        ProductoResponse responseMapped = ProductoResponse.builder()
                .id(2L)
                .nombre("Phone")
                .descripcion("Smartphone")
                .precio(800.0)
                .build();

        Page<ProductoEntity> pageEntities = new PageImpl<>(List.of(entity));

        when(productoInterface.getAll(page, size, sortDirection, sortBy)).thenReturn(pageEntities);
        when(mapper.mapToResponse(entity)).thenReturn(responseMapped);
        when(utilService.getImagenBase64("images/phone.png"))
                .thenThrow(new CustomException("Error imagen"));

        // Act
        ResponseEntity<ApiResponse> response = productoController.getAll(page, size, sortDirection, sortBy);

        // Assert
        ApiResponse apiResponse = response.getBody();
        assertNotNull(apiResponse);
        Page<?> pageResult = (Page<?>) apiResponse.getData();
        ProductoResponse productoResponse = (ProductoResponse) pageResult.getContent().get(0);

        assertNull(productoResponse.getImagenBase64());
    }

    @Test
    @DisplayName("Debe retornar ResponseEntity con el producto cuando existe")
    void getByIdWhenProductoExists() throws CustomException {
        Long id = 1L;

        ProductoEntity entity = ProductoEntity.builder()
                .id(id)
                .nombre("Laptop")
                .descripcion("Gaming laptop")
                .precio(1500.0)
                .build();

        ProductoResponse responseMapped = ProductoResponse.builder()
                .id(id)
                .nombre("Laptop")
                .descripcion("Gaming laptop")
                .precio(1500.0)
                .build();

        when(productoInterface.getById(id)).thenReturn(entity);
        when(mapper.mapToResponse(entity)).thenReturn(responseMapped);

        ResponseEntity<ApiResponse> response = productoController.getById(id);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        ApiResponse apiResponse = response.getBody();
        assertNotNull(apiResponse);
        assertEquals(responseMapped, apiResponse.getData());

        verify(productoInterface, times(1)).getById(id);
        verify(mapper, times(1)).mapToResponse(entity);
    }

    @Test
    @DisplayName("Debe lanzar CustomException cuando el producto no existe")
    void getByIdWhenProductoNotFound() throws CustomException {
        Long id = 99L;
        when(productoInterface.getById(id))
                .thenThrow(new CustomException(MensajeEmun.NOT_FOUND.getMsg()));

        CustomException exception = assertThrows(CustomException.class, () -> productoController.getById(id));
        assertEquals(MensajeEmun.NOT_FOUND.getMsg(), exception.getMessage());

        verify(productoInterface, times(1)).getById(id);
        verify(mapper, never()).mapToResponse(any());
    }

    @Test
    @DisplayName("Debe retornar ResponseEntity con el producto actualizado cuando existe")
    void updateWhenProductoUpdated() throws CustomException {
        Long id = 1L;

        ProductoRequest request = ProductoRequest.builder()
                .nombre("Laptop Gamer")
                .descripcion("Nueva descripción")
                .precio(2000.0)
                .build();

        ProductoEntity entityUpdated = ProductoEntity.builder()
                .id(id)
                .nombre("Laptop Gamer")
                .descripcion("Nueva descripción")
                .precio(2000.0)
                .build();

        ProductoResponse responseMapped = ProductoResponse.builder()
                .id(id)
                .nombre("Laptop Gamer")
                .descripcion("Nueva descripción")
                .precio(2000.0)
                .build();

        when(productoInterface.updateById(id, request)).thenReturn(entityUpdated);
        when(mapper.mapToResponse(entityUpdated)).thenReturn(responseMapped);

        ResponseEntity<ApiResponse> response = productoController.update(id, request);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        ApiResponse apiResponse = response.getBody();
        assertNotNull(apiResponse);
        assertEquals(responseMapped, apiResponse.getData());

        verify(productoInterface, times(1)).updateById(id, request);
        verify(mapper, times(1)).mapToResponse(entityUpdated);
    }

    @Test
    @DisplayName("Debe lanzar CustomException cuando el servicio falla al actualizar")
    void updateWhenServiceFails() throws CustomException {
        Long id = 99L;

        ProductoRequest request = ProductoRequest.builder()
                .nombre("Phone")
                .descripcion("Smartphone")
                .precio(800.0)
                .build();

        when(productoInterface.updateById(id, request))
                .thenThrow(new CustomException("Error al actualizar"));

        CustomException exception = assertThrows(CustomException.class, () -> productoController.update(id, request));
        assertTrue(exception.getMessage().contains("Error al actualizar"));

        verify(productoInterface, times(1)).updateById(id, request);
        verify(mapper, never()).mapToResponse(any());
    }
}