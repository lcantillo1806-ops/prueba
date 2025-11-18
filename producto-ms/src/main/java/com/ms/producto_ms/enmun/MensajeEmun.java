package com.ms.producto_ms.enmun;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Enumeración que define los mensajes de error utilizados en la aplicación.
 * <p>
 * Cada constante del enum representa un mensaje específico que puede ser
 * mostrado al usuario o utilizado en el manejo de excepciones.
 * <p>
 * El uso de {@link lombok.RequiredArgsConstructor} junto con {@link lombok.Getter}
 * permite inicializar y acceder al mensaje asociado a cada constante de forma sencilla.
 * <p>
 * Ejemplo de uso:
 * <pre>
 *     throw new CustomException(MensajeEmun.ERROR_GENERAL.getMsg());
 * </pre>
 *
 * @author Luis Cantillo
 * @since 1.0.0
 */
@Getter
@RequiredArgsConstructor
public enum MensajeEmun {
    /**
     * Indica que no se encontraron productos en el sistema.
     */
    NOT_FOUNDS("No se encontraron productos registrados en el sistema."),
    /**
     * Indica que no se encontraron productos en el sistema.
     */
    NOT_FOUND("No se encontró producto registrado en el sistema con el id ingresado."),
    /**
     * Error genérico al guardar información.
     */
    ERROR_GENERAL("No se pudo realizar el %s, porfavor comuniquese con soporte tecnico.");

    /**
     * Texto descriptivo del mensaje asociado a la constante.
     */
    private final String msg;
}
