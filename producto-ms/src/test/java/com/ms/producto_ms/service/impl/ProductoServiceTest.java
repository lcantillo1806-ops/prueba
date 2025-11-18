package com.ms.producto_ms.service.impl;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.ms.producto_ms.dto.request.ProductoRequest;
import com.ms.producto_ms.enmun.MensajeEmun;
import com.ms.producto_ms.entity.ProductoEntity;
import com.ms.producto_ms.exception.CustomException;
import com.ms.producto_ms.repository.ProductoRepository;
import com.ms.producto_ms.util.UtilService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private ProductoService productoService;

    @Mock
    private UtilService utilService;

    @Test
    @DisplayName("Debe guardar un producto correctamente cuando la solicitud es válida")
    void save_WhenRequestIsValid() throws CustomException, IOException {
        // Arrange
        ProductoRequest request = ProductoRequest.builder()
                .nombre("Laptop")
                .descripcion("Gaming laptop")
                .precio(1500.0)
                .build();

        String fakePath = "images/laptop.png";

        when(utilService.getUrlPaths(request)).thenReturn(fakePath);

        ProductoEntity expectedEntity = ProductoEntity.builder()
                .nombre("Laptop")
                .descripcion("Gaming laptop")
                .precio(1500.0)
                .imagenPath(fakePath)
                .build();

        when(productoRepository.save(any(ProductoEntity.class))).thenReturn(expectedEntity);

        // Act
        ProductoEntity result = productoService.save(request);

        // Assert
        assertNotNull(result);
        assertEquals("Laptop", result.getNombre());
        assertEquals("Gaming laptop", result.getDescripcion());
        assertEquals(1500.0, result.getPrecio());
        assertEquals(fakePath, result.getImagenPath());

        verify(productoRepository, times(1)).save(any(ProductoEntity.class));
    }

    @Test
    @DisplayName("Debe lanzar CustomException cuando el repositorio falla al guardar")
    void saveRepositoryFails() throws IOException {
        // Arrange
        ProductoRequest request = ProductoRequest.builder()
                .nombre("Phone")
                .descripcion("Smartphone")
                .precio(800.0)
                .build();

        when(utilService.getUrlPaths(request)).thenReturn("images/laptop.png");
        when(productoRepository.save(any(ProductoEntity.class)))
                .thenThrow(new RuntimeException("DB error"));

        // Act & Assert
        CustomException exception = assertThrows(CustomException.class, () -> productoService.save(request));
        assertTrue(exception.getMessage().contains("guardado"));
    }

    @Test
    @DisplayName("Debe retornar una página con productos cuando existen datos")
    void getAllDataExists() throws CustomException {
        // Arrange
        int page = 0;
        int size = 10;
        String sortDirection = "asc";
        String sortBy = "id";

        ProductoEntity producto = ProductoEntity.builder()
                .nombre("Laptop")
                .descripcion("Gaming laptop")
                .precio(1500.0)
                .build();

        Page<ProductoEntity> pageResult = new PageImpl<>(List.of(producto));

        // El servicio construye internamente el Pageable, por eso usamos any(Pageable.class)
        when(productoRepository.findAll(any(Pageable.class))).thenReturn(pageResult);

        // Act
        Page<ProductoEntity> result = productoService.getAll(page, size, sortDirection, sortBy);

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.getTotalElements());
        assertEquals("Laptop", result.getContent().get(0).getNombre());

        verify(productoRepository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @DisplayName("Debe lanzar CustomException cuando no se encuentran productos")
    void getAllNoDataFound() {
        // Arrange
        int page = 0;
        int size = 10;
        String sortDirection = "asc";
        String sortBy = "id";

        Page<ProductoEntity> emptyPage = Page.empty();
        when(productoRepository.findAll(any(Pageable.class))).thenReturn(emptyPage);

        // Act & Assert
        CustomException exception = assertThrows(
                CustomException.class,
                () -> productoService.getAll(page, size, sortDirection, sortBy)
        );
        assertEquals(MensajeEmun.NOT_FOUNDS.getMsg(), exception.getMessage());

        verify(productoRepository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @DisplayName("Debe retornar el producto cuando existe en el repositorio")
    void getByIdWhenExists() throws CustomException {
        // Arrange
        Long id = 1L;
        ProductoEntity producto = ProductoEntity.builder()
                .id(id)
                .nombre("Laptop")
                .descripcion("Gaming laptop")
                .precio(1500.0)
                .build();

        when(productoRepository.findById(id)).thenReturn(Optional.of(producto));

        // Act
        ProductoEntity result = productoService.getById(id);

        // Assert
        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals("Laptop", result.getNombre());
        verify(productoRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("Debe lanzar CustomException cuando el producto no existe")
    void getByIdWhenNotFound() {
        // Arrange
        Long id = 99L;
        when(productoRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        CustomException exception = assertThrows(CustomException.class, () -> productoService.getById(id));
        assertEquals(MensajeEmun.NOT_FOUND.getMsg(), exception.getMessage());
        verify(productoRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("Debe eliminar el producto cuando existe en el repositorio")
    void deleteWhenExists() throws CustomException {
        Long id = 1L;
        when(productoRepository.existsById(id)).thenReturn(true);

        productoService.delete(id);

        verify(productoRepository, times(1)).existsById(id);
        verify(productoRepository, times(1)).deleteById(id);
        verify(utilService, never()).notFoundMsg();
    }

    @Test
    @DisplayName("Debe lanzar CustomException cuando el producto no existe")
    void deleteWhenNotExists() throws CustomException {
        Long id = 99L;
        when(productoRepository.existsById(id)).thenReturn(false);
        doThrow(new CustomException(MensajeEmun.NOT_FOUND.getMsg())).when(utilService).notFoundMsg();

        CustomException exception = assertThrows(CustomException.class, () -> productoService.delete(id));
        assertEquals(MensajeEmun.NOT_FOUND.getMsg(), exception.getMessage());

        verify(productoRepository, times(1)).existsById(id);
        verify(productoRepository, never()).deleteById(id);
        verify(utilService, times(1)).notFoundMsg();
    }

    @Test
    @DisplayName("Debe lanzar CustomException cuando ocurre un error inesperado")
    void deleteWhenUnexpectedError() {
        Long id = 5L;
        when(productoRepository.existsById(id)).thenThrow(new RuntimeException("DB error"));

        CustomException exception = assertThrows(CustomException.class, () -> productoService.delete(id));
        assertTrue(exception.getMessage().contains("eliminado"));

        verify(productoRepository, times(1)).existsById(id);
        verify(productoRepository, never()).deleteById(id);
    }

    @Test
    @DisplayName("Debe actualizar el producto correctamente cuando existe en el repositorio")
    void updateByIdWhenExists() throws CustomException {
        Long id = 1L;
        ProductoRequest request = ProductoRequest.builder()
                .nombre("Laptop Gamer")
                .descripcion("Nueva descripción")
                .precio(2000.0)
                .build();

        ProductoEntity existing = ProductoEntity.builder()
                .id(id)
                .nombre("Laptop")
                .descripcion("Gaming laptop")
                .precio(1500.0)
                .build();

        when(productoRepository.findById(id)).thenReturn(Optional.of(existing));
        when(productoRepository.save(any(ProductoEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ProductoEntity result = productoService.updateById(id, request);

        assertNotNull(result);
        assertEquals("Laptop Gamer", result.getNombre());
        assertEquals("Nueva descripción", result.getDescripcion());
        assertEquals(2000.0, result.getPrecio());

        verify(productoRepository, times(1)).findById(id);
        verify(productoRepository, times(1)).save(existing);
    }

    @Test
    @DisplayName("Debe lanzar CustomException cuando el producto no existe")
    void updateByIdCustomExceptionWhenNotFound() throws CustomException {
        Long id = 99L;
        ProductoRequest request = ProductoRequest.builder()
                .nombre("Phone")
                .descripcion("Smartphone")
                .precio(800.0)
                .build();

        when(productoRepository.findById(id)).thenReturn(Optional.empty());
        doThrow(new CustomException(MensajeEmun.NOT_FOUND.getMsg())).when(utilService).notFoundMsg();

        CustomException exception = assertThrows(CustomException.class, () -> productoService.updateById(id, request));
        assertEquals(MensajeEmun.NOT_FOUND.getMsg(), exception.getMessage());

        verify(productoRepository, times(1)).findById(id);
        verify(productoRepository, never()).save(any());
        verify(utilService, times(1)).notFoundMsg();
    }

    @Test
    @DisplayName("Debe lanzar CustomException cuando ocurre un error inesperado")
    void updateByIdWhenUnexpectedError() {
        Long id = 5L;
        ProductoRequest request = ProductoRequest.builder()
                .nombre("Tablet")
                .descripcion("Android tablet")
                .precio(500.0)
                .build();

        when(productoRepository.findById(id)).thenThrow(new RuntimeException("DB error"));

        CustomException exception = assertThrows(CustomException.class, () -> productoService.updateById(id, request));
        assertTrue(exception.getMessage().contains("actualizado"));

        verify(productoRepository, times(1)).findById(id);
        verify(productoRepository, never()).save(any());
    }
}