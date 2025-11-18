package com.ms.producto_ms.service.impl;

import com.ms.producto_ms.dto.request.ProductoRequest;
import com.ms.producto_ms.enmun.MensajeEmun;
import com.ms.producto_ms.entity.ProductoEntity;
import com.ms.producto_ms.exception.CustomException;
import com.ms.producto_ms.repository.ProductoRepository;
import com.ms.producto_ms.service.ProductoInterface;
import com.ms.producto_ms.util.UtilService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;


/**
 * Interfaz que define las operaciones de negocio relacionadas con la entidad {@link ProductoEntity}.
 * <p>
 * Esta interfaz establece el contrato para la gestión de productos en el sistema,
 * incluyendo creación, consulta, actualización y eliminación. Las implementaciones
 * concretas deben encargarse de la lógica de negocio y la interacción con el repositorio.
 *
 * @author Luis Cantillo
 * @since 1.0.0
 */

@RequiredArgsConstructor
@Service
public class ProductoService implements ProductoInterface {
    private final ProductoRepository productoRepository;
    private final UtilService util;

    @Override
    public ProductoEntity save(ProductoRequest request) throws CustomException {
        try {
            var path= util.getUrlPaths(request);
            ProductoEntity producto = ProductoEntity.builder()
                    .nombre(request.getNombre())
                    .descripcion(request.getDescripcion())
                    .precio(request.getPrecio())
                    .imagenPath(path)
                    .build();
            return productoRepository.save(producto);
        } catch (Exception e) {
            throw new CustomException(MensajeEmun.ERROR_GENERAL.getMsg().formatted("guardado"));
        }
    }


    @Override
    public Page<ProductoEntity> getAll(int page,
                                       int size,
                                       String sortDirection,
                                       String sortBy) throws CustomException {

        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        String sortProperty = (sortBy == null || sortBy.isBlank()) ? "id" : sortBy;

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortProperty));

        Page<ProductoEntity> result = productoRepository.findAll(pageable);

        if (result.isEmpty()) {
            throw new CustomException(MensajeEmun.NOT_FOUNDS.getMsg(), HttpStatus.NOT_FOUND);
        }

        return result;
    }

    @Override
    public ProductoEntity getById(Long id) throws CustomException {
        return productoRepository.findById(id)
                .orElseThrow(() -> new CustomException(MensajeEmun.NOT_FOUND.getMsg(), HttpStatus.BAD_REQUEST));
    }

    @Override
    public void delete(Long id) throws CustomException {
        try {
            var exist = productoRepository.existsById(id);
            if (!exist) {
                util.notFoundMsg();
            }
            productoRepository.deleteById(id);
        } catch (CustomException e) {
            throw new CustomException(e.getMessage(),e.getStatus());
        } catch (Exception e) {
            throw new CustomException(MensajeEmun.ERROR_GENERAL.getMsg().formatted("eliminado"));
        }
    }


    @Override
    public ProductoEntity updateById(Long id, ProductoRequest request) throws CustomException {

        try {
            Optional<ProductoEntity> result = productoRepository.findById(id);
            if (result.isEmpty()) {
                util.notFoundMsg();
            }
            ProductoEntity item = result.get();
            item.setNombre(request.getNombre());
            item.setDescripcion(request.getDescripcion());
            item.setPrecio(request.getPrecio());
            return productoRepository.save(item);
        } catch (CustomException e) {
            throw new CustomException(e.getMessage(),e.getStatus());
        } catch (Exception e) {
            throw new CustomException(MensajeEmun.ERROR_GENERAL.getMsg().formatted("actualizado"));
        }
    }


}
