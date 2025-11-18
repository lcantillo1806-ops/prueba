package com.ms.producto_ms.service;

import com.ms.producto_ms.dto.request.ProductoRequest;
import com.ms.producto_ms.entity.ProductoEntity;
import com.ms.producto_ms.exception.CustomException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Interfaz que define las operaciones de negocio relacionadas con la entidad {@link ProductoEntity}.
 * <p>
 * Esta interfaz establece el contrato para la gestión de productos en el sistema,
 * incluyendo creación, consulta, actualización y eliminación. Las implementaciones
 * concretas deben encargarse de la lógica de negocio y la interacción con el repositorio.
 * <p>
 * Métodos típicos:
 * <ul>
 *   <li>findAll(Pageable pageable)</li>
 * </ul>
 *
 * También se pueden definir consultas adicionales utilizando la convención
 * de nombres de Spring Data JPA.
 *
 * @author Luis Cantillo
 * @since 1.0.0
 */
public interface ProductoInterface {

    /**
     * Crea un nuevo producto en el sistema a partir de la información
     * proporcionada en el request. Este metodo valida los datos recibidos y construye una instancia
     * de {@link ProductoEntity}, la cual se persiste en la base de datos.
     * En caso de que ocurra un error durante la creación (por ejemplo,
     * datos inválidos o problemas de persistencia), se lanza una
     * {@link CustomException}.
     *
     * @param request objeto {@link ProductoRequest} que contiene los datos
     *                necesarios para crear el producto (nombre, descripción,
     *                precio, disponibilidad).
     * @return la entidad {@link ProductoEntity} creada y persistida en la base de datos.
     * @throws CustomException si ocurre un error en la validación o en la operación de persistencia.
     * @since 1.0.0
     */
    ProductoEntity save(ProductoRequest request) throws CustomException;

    /**
     * Obtiene una lista paginada de productos registrados en el sistema.
     *
     * <p>Este metodo permite consultar los productos utilizando un objeto {@link Pageable}
     * para definir parámetros de paginación y ordenamiento (por ejemplo, número de página,
     * tamaño de página y criterios de orden). La consulta retorna un objeto {@link Page}
     * que contiene entidades de tipo {@link ProductoEntity}.
     *
     * <p>En caso de que ocurra algún error durante la operación, se lanza una
     * {@link CustomException}.
     *
     * @param pageable objeto que define la información de paginación y ordenamiento
     * @return una página de entidades {@link ProductoEntity} según los parámetros de paginación
     * @throws CustomException si ocurre un error al obtener los productos
     * @since 1.0.0
     */
    Page<ProductoEntity> getAll(int page,
                                int size,
                                String sortDirection,
                                String sortBy) throws CustomException;

    /**
     * Obtiene un producto por su identificador único.
     *
     * <p>Este metodo busca un {@link ProductoEntity} en el sistema utilizando
     * el valor del identificador proporcionado. Si el producto no existe,
     * se lanza una {@link CustomException}.
     *
     * @param id identificador único del producto
     * @return la entidad {@link ProductoEntity} correspondiente al identificador
     * @throws CustomException si no se encuentra el producto o ocurre un error en la consulta
     * @since 1.0.0
     */
    ProductoEntity getById(Long id) throws CustomException;

    /**
     * Elimina un producto por su identificador único.
     *
     * <p>Este metodo busca un producto en el sistema utilizando el {@code id}
     * proporcionado y lo elimina si existe. Si el producto no se encuentra,
     * se lanza una {@link CustomException}.
     *
     * @param id identificador único del producto a eliminar
     * @throws CustomException si el producto no existe o ocurre un error durante la eliminación
     * @since 1.0.0
     */
    void delete(Long id) throws CustomException;

    /**
     * Actualiza un producto existente en el sistema utilizando su identificador único.
     *
     * <p>Este metodo busca un {@link ProductoEntity} por el {@code id} proporcionado
     * y reemplaza sus datos con la información contenida en el objeto {@link ProductoRequest}.
     * Si el producto no existe, se lanza una {@link CustomException}.
     *
     * @param id identificador único del producto a actualizar
     * @param request objeto con los nuevos datos del producto
     * @return la entidad {@link ProductoEntity} actualizada
     * @throws CustomException si el producto no existe o ocurre un error durante la actualización
     * @since 1.0.0
     */
    ProductoEntity updateById(Long id, ProductoRequest request) throws CustomException;

}
