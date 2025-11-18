package com.ms.producto_ms.controller;

import com.ms.producto_ms.config.OpenApiConfig;
import com.ms.producto_ms.dto.request.ProductoRequest;
import com.ms.producto_ms.dto.response.ApiResponse;
import com.ms.producto_ms.dto.response.ProductoResponse;
import com.ms.producto_ms.entity.ProductoEntity;
import com.ms.producto_ms.exception.CustomException;
import com.ms.producto_ms.mapper.ProductoMapper;
import com.ms.producto_ms.service.ProductoInterface;
import com.ms.producto_ms.util.UtilService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;


@Slf4j
@CrossOrigin(allowedHeaders = "*", origins = "*")
@RestController
@RequestMapping(value = "/api/productos")
@AllArgsConstructor
@SecurityRequirement(name = OpenApiConfig.API_KEY_SCHEME_NAME)
@Tag(name = "Productos Resource",
        description = "Se encarga de exponer los endpoints del microservicio Productos MS, gestionando operaciones CRUD sobre el catálogo de productos. Recibe solicitudes HTTP, valida datos, delega la lógica al servicio de productos y devuelve respuestas estructuradas con códigos adecuados.")
public class ProductoController extends BaseController {

    private final ProductoInterface productoInterface;
    private final UtilService utilService;
    private final ProductoMapper mapper;

    /**
     * Endpoint encargado de registrar un nuevo producto en el sistema.
     *
     * <p>Este metodo recibe un objeto {@link ProductoRequest} con la información
     * necesaria para crear el producto. La solicitud debe enviarse en formato JSON
     * y cumplir con las validaciones definidas en la clase de entrada.
     *
     * <p>En caso de éxito, retorna un objeto {@link ApiResponse} con los detalles
     * del producto guardado y un mensaje de confirmación. Si ocurre algún error
     * durante el proceso, se lanza una {@link CustomException}.
     *
     * @param body objeto con la información del producto a registrar
     * @return ResponseEntity que contiene un {@link ApiResponse} con el resultado de la operación
     * @throws CustomException si ocurre un error durante el guardado del producto
     * @since 1.0.0
     */
    @Operation(
            summary = "Registrar un nuevo producto",
            description = "Método POST encargado de guardar un producto en el sistema. "
                    + "Recibe un objeto JSON con los datos del producto y retorna "
                    + "una respuesta con el resultado de la operación."
    )
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse> save(
            @Parameter(description = "Datos del producto a registrar", required = true)
            @RequestBody @Valid ProductoRequest body) throws CustomException {
        productoInterface.save(body);
        return createSuccessResponse("Producto creado exitosamente.");
    }


    /**
     * Obtiene una lista paginada de productos.
     *
     * <p>Este endpoint expone un metodo GET que recibe un objeto {@link Pageable}
     * con los parámetros de paginación y ordenamiento. Retorna un objeto
     * {@link ApiResponse} con la información de los productos.
     *
     * @return ResponseEntity con la lista paginada de productos
     * @throws CustomException si no existen productos registrados
     * @since 1.0.0
     */
    @Operation(
            summary = "Listar productos",
            description = "Endpoint GET encargado de obtener todos los productos registrados en el sistema. "
                    + "Soporta parámetros de paginación (`page`, `size`) y ordenamiento (`sortDirection`, `sortBy`).")
    @GetMapping
    public ResponseEntity<ApiResponse> getAll(
            @Parameter(description = "Número de página (0 basado en índice)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Cantidad de registros por página", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Dirección de ordenamiento: asc o desc", example = "asc")
            @RequestParam(defaultValue = "asc") String sortDirection,
            @Parameter(description = "Campo por el cual ordenar (ej: id, nombre)", example = "id")
            @RequestParam(defaultValue = "id") String sortBy
    ) throws CustomException {

        Page<ProductoEntity> result = productoInterface.getAll(page, size, sortDirection, sortBy);

        Page<ProductoResponse> items = result.map(entity -> {
            ProductoResponse response = mapper.mapToResponse(entity);
            response.setImagenBase64(
                    Optional.ofNullable(entity.getImagenPath())
                            .map(path -> {
                                try {
                                    return utilService.getImagenBase64(path);
                                } catch (CustomException ex) {
                                    log.warn("No se pudo convertir la imagen de producto {}: {}", entity.getId(), ex.getMessage());
                                    return null;
                                }
                            }).orElse(null)
            );
            return response;
        });

        return createSuccessResponse(items);
    }


    /**
     * Obtiene un producto específico por su identificador único.
     *
     * <p>Este endpoint expone un metodo GET que recibe el parámetro {@code id}
     * en la URL y retorna la información del producto correspondiente en un
     * objeto {@link ApiResponse}.
     *
     * @param id identificador único del producto
     * @return ResponseEntity con la información del producto encontrado
     * @throws CustomException si el producto no existe o ocurre un error en la consulta
     * @since 1.0.0
     */
    @Operation(
            summary = "Obtener producto por ID",
            description = "Endpoint GET encargado de consultar un producto específico en el sistema "
                    + "utilizando su identificador único."
    )
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse> getById(
            @Parameter(description = "Identificador único del producto", required = true, example = "1")
            @PathVariable Long id) throws CustomException {
        var item = productoInterface.getById(id);
        var result = mapper.mapToResponse(item);
        result.setImagenBase64(utilService.getImagenBase64(item.getImagenPath()));
        return createSuccessResponse(result);
    }

    /**
     * Elimina un producto por su identificador único.
     *
     * <p>Este endpoint expone un metodo DELETE que recibe el parámetro {@code id}
     * en la solicitud y elimina el producto correspondiente del sistema.
     * Si el producto no existe, se lanza una {@link CustomException}.
     *
     * @param id identificador único del producto a eliminar
     * @return ResponseEntity vacío con código de estado 204 (No Content) si la eliminación fue exitosa
     * @throws CustomException si el producto no existe o ocurre un error durante la eliminación
     * @since 1.0.0
     */
    @Operation(
            summary = "Eliminar producto por ID",
            description = "Endpoint DELETE encargado de eliminar un producto específico del sistema "
                    + "utilizando su identificador único."
    )
    @DeleteMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse> delete(
            @Parameter(description = "Identificador único del producto", required = true, example = "1")
            @RequestParam("id") Long id) throws CustomException {
        productoInterface.delete(id);
        return createSuccessResponse("Producto eliminadodo existosamente.");
    }

    /**
     * Actualiza un producto existente en el sistema.
     *
     * <p>Este endpoint expone un metodo PATCH que recibe el identificador único
     * del producto en la URL y un objeto {@link ProductoRequest} con los nuevos
     * datos a actualizar. Si el producto no existe, se lanza una {@link CustomException}.
     *
     * @param id   identificador único del producto a actualizar
     * @param body objeto con los datos del producto a modificar
     * @return ResponseEntity con la información del producto actualizado
     * @throws CustomException si el producto no existe o ocurre un error durante la actualización
     */
    @Operation(
            summary = "Actualizar producto por ID",
            description = "Endpoint PATCH encargado de modificar los datos de un producto existente "
                    + "utilizando su identificador único y un objeto JSON con los nuevos valores."
    )
    @PatchMapping(value = "/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse> update(
            @Parameter(description = "Identificador único del producto", required = true, example = "1")
            @PathVariable Long id,
            @Parameter(description = "Datos del producto a actualizar", required = true)
            @RequestBody ProductoRequest body) throws CustomException {
        ProductoEntity item = productoInterface.updateById(id, body);
        var result = mapper.mapToResponse(item);
        return createSuccessResponse(result);
    }
}
