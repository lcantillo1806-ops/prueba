package com.ms.producto_ms.dto.response;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Builder
@JsonPropertyOrder({ "code", "message", "data" })
public class ApiResponse implements Serializable {
    private Map<String, String> jsonapi;
    private Object data;
    private String message;
    private Integer code;

}
