package com.ms.producto_ms.repository;

import com.ms.producto_ms.entity.ProductoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

/**
 * Proporciona operaciones CRUD y consultas personalizadas sobre la tabla "productos".
 * Extiende {@link JpaRepository}, lo que permite acceder a métodos como:
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
@Repository
public interface ProductoRepository  extends JpaRepository<ProductoEntity, Long> {
    Page<ProductoEntity> findAll(Pageable pageable);
}
