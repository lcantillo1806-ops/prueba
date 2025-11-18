package com.ms.producto_ms.repository;

import com.ms.producto_ms.entity.InventarioEntity;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Proporciona operaciones CRUD y consultas personalizadas sobre la tabla "inventarios".
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
public interface InventarioRepository extends JpaRepository<InventarioEntity, Long> {

    @Query("""
        SELECT COALESCE(
            SUM(
                CASE WHEN i.tipoMovimiento = 'INGRESO' THEN i.cantidad
                     WHEN i.tipoMovimiento = 'SALIDA'  THEN -i.cantidad
                     ELSE 0
                END
            ), 0
        )
        FROM InventarioEntity i
        WHERE i.productoId = :productoId
        """)
    int obtenerSaldoPorProducto(@Param("productoId") Long productoId);

     Optional<InventarioEntity> findTopByProductoIdOrderByFechaMovimientoDesc(Long productoId);
}
