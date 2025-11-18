package com.ms.producto_ms.controller;

import com.ms.producto_ms.dto.request.MovimientoInventarioRequest;
import com.ms.producto_ms.dto.response.ApiResponse;
import com.ms.producto_ms.dto.response.InventarioDetalleResponse;
import com.ms.producto_ms.exception.CustomException;
import com.ms.producto_ms.mapper.ProductoMapper;
import com.ms.producto_ms.service.InventarioInterface;
import com.ms.producto_ms.util.UtilService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@Slf4j
@CrossOrigin(allowedHeaders = "*", origins = "*")
@RestController
@RequestMapping(value = "/api/inventarios/productos")
@AllArgsConstructor
@Tag(name = "Productos Resource",
        description = "Se encarga de exponer los endpoints del microservicio Productos MS, gestionando operaciones CRUD sobre el catálogo de productos. Recibe solicitudes HTTP, valida datos, delega la lógica al servicio de productos y devuelve respuestas estructuradas con códigos adecuados.")
public class InventarioController extends BaseController {

    private final InventarioInterface inventarioInterface;

    // ====== CONSULTAR DETALLE ======
    @Operation(
            summary = "Obtener detalle de inventario por producto",
            description = "Devuelve el nombre del producto, la cantidad disponible, el precio unitario y el valor total."
    )
    @GetMapping(value = "/{productoId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse> getDetalle(
            @Parameter(description = "ID del producto", required = true, example = "1")
            @PathVariable Long productoId
    ) throws CustomException {

        InventarioDetalleResponse detalle = inventarioInterface.consultarDetalleInventario(productoId);

        ApiResponse response = ApiResponse.builder()
                .code(HttpStatus.OK.value())
                .message("Detalle de inventario consultado correctamente.")
                .data(detalle)
                .jsonapi(Map.of("version", "1.0"))
                .build();


        return ResponseEntity.ok(response);
    }

    // ====== INGRESO ======
    @Operation(
            summary = "Registrar ingreso de inventario",
            description = "Registra un movimiento de INGRESO y devuelve la nueva cantidad disponible."
    )
    @PostMapping(value = "/{productoId}/ingreso", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse> ingresar(
            @PathVariable Long productoId,
            @RequestBody @Valid MovimientoInventarioRequest body
    ) throws CustomException {

        int nuevaCantidad = inventarioInterface.agregarCantidad(productoId, body.getCantidad(), body.getPrecioUnitario());

        ApiResponse response = ApiResponse.builder()
                .code(HttpStatus.OK.value())
                .message("Ingreso registrado correctamente.")
                .data(Map.of(
                        "productoId", productoId,
                        "cantidadNueva", nuevaCantidad
                ))
                .jsonapi(Map.of("version", "1.0"))
                .build();

        return ResponseEntity.ok(response);
    }

    // ====== SALIDA ======
    @Operation(
            summary = "Registrar salida de inventario",
            description = "Registra un movimiento de SALIDA y devuelve la nueva cantidad disponible."
    )
    @PostMapping(value = "/{productoId}/salida", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse> retirar(
            @PathVariable Long productoId,
            @RequestBody @Valid MovimientoInventarioRequest body
    ) throws CustomException {

        int nuevaCantidad = inventarioInterface.retirarCantidad(productoId, body.getCantidad(), body.getPrecioUnitario());

        ApiResponse response = ApiResponse.builder()
                .code(HttpStatus.OK.value())
                .message("Salida registrada correctamente.")
                .data(Map.of(
                        "productoId", productoId,
                        "cantidadNueva", nuevaCantidad
                ))
                .jsonapi(Map.of("version", "1.0"))
                .build();

        return ResponseEntity.ok(response);
    }

}
