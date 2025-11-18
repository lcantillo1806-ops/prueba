package com.ms.producto_ms.util;

import com.ms.producto_ms.dto.request.ProductoRequest;
import com.ms.producto_ms.enmun.MensajeEmun;
import com.ms.producto_ms.exception.CustomException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Objects;
import java.util.UUID;

@Component
public class UtilService {

    @Value("${app.upload.dir}")
    private String uploadDir;

    /**
     * Lanza una excepción personalizada indicando que un recurso no fue encontrado.
     * <p>
     * Este metodo se utiliza para centralizar el manejo de errores de tipo "Not Found"
     * en la aplicación. Al invocarse, arroja una {@link CustomException} con el mensaje
     * definido en {@link MensajeEmun#NOT_FOUND} y el código de estado HTTP 404 (NOT_FOUND).
     * </p>
     *
     * @throws CustomException siempre que se invoque, indicando que el recurso solicitado
     *                         no existe o no pudo ser localizado.
     */
    public void notFoundMsg() throws CustomException {
        throw new CustomException(MensajeEmun.NOT_FOUND.getMsg(), HttpStatus.NOT_FOUND);
    }


    /**
     * Obtiene la imagen en formato Base64 a partir de la ruta almacenada en el request.
     *
     * @param ruta ruta de la imagen.
     * @return la imagen codificada en Base64 como String, o null si no existe la ruta.
     * @throws CustomException si ocurre un error al leer el archivo.
     * @since 1.0.0
     */
    public String getImagenBase64(String ruta) throws CustomException {
        try {
            if (Objects.nonNull(ruta)) {
                Path path = Paths.get(ruta);
                byte[] fileBytes = Files.readAllBytes(path);
                return Base64.getEncoder().encodeToString(fileBytes);
            }
        } catch (IOException e) {
            throw new CustomException(e.getMessage(), e);
        }
        return null;
    }


    /**
     * Genera y devuelve la ruta de almacenamiento de la imagen asociada a un producto.
     * <p>
     * Este metodo toma el objeto {@link ProductoRequest}, extrae la información necesaria
     * (por ejemplo, el nombre del producto o un identificador único) y construye una ruta
     * de archivo donde se almacenará la imagen. La ruta puede ser relativa o absoluta
     * dependiendo de la configuración del sistema.
     * </p>
     *
     * @param request el objeto que contiene los datos del producto, incluyendo la imagen en Base64.
     * @return la ruta generada en forma de cadena, donde se almacenará la imagen.
     * @throws IOException si ocurre un error al crear o escribir el archivo en el sistema de almacenamiento.
     * @since 1.0.0
     */
    public String getUrlPaths(ProductoRequest request) throws IOException {
        if (Objects.nonNull(request.getImagenPath())) {
            // 1. Convertir base64 a bytes
            byte[] fileBytes = Base64.getDecoder().decode(request.getImagenPath());

            // 2. Asegurar que el directorio de uploads exista
            Path dir = Paths.get(uploadDir);
            if (Files.notExists(dir)) {
                Files.createDirectories(dir);
            }

            // 3. Generar nombre de archivo
            Path path = dir.resolve(UUID.randomUUID() + ".png");

            // 4. Guardar archivo en disco
            Files.write(path, fileBytes);

            // 5. Retornar ruta absoluta
            return path.toString();
        }
        return null;
    }
}
