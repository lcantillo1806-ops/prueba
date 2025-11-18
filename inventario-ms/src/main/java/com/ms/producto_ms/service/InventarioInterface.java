package com.ms.producto_ms.service;


import com.ms.producto_ms.dto.response.InventarioDetalleResponse;
import com.ms.producto_ms.entity.InventarioEntity;
import com.ms.producto_ms.exception.CustomException;

import java.math.BigDecimal;

/**
 * Interfaz que define las operaciones de negocio relacionadas con la entidad {@link InventarioEntity}.
 * <p>
 * Esta interfaz establece el contrato para la gestión de productos en el sistema,
 * incluyendo creación, consulta, actualización y eliminación. Las implementaciones
 * concretas deben encargarse de la lógica de negocio y la interacción con el repositorio.
 * <p>
 * Métodos típicos:
 * <ul>
 *   <li>findAll(Pageable pageable)</li>
 * </ul>
 * <p>
 * También se pueden definir consultas adicionales utilizando la convención
 * de nombres de Spring Data JPA.
 *
 * @author Luis Cantillo
 * @since 1.0.0
 */
public interface    InventarioInterface {


    int agregarCantidad(Long productoId, Integer cantidad, BigDecimal precioUnitario)  throws CustomException;

    int retirarCantidad(Long productoId, Integer cantidad, BigDecimal precioUnitario)  throws CustomException;

    InventarioDetalleResponse consultarDetalleInventario(Long productoId) throws CustomException;
}
