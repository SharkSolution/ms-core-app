package com.suresell.mscoreapp.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class CreateMenuProductRequest {
    @NotBlank(message = "ID de producto es obligatorio")
    private String id;
    
    @NotBlank(message = "Nombre de producto es obligatorio")
    private String name;
    
    @NotNull(message = "Precio es obligatorio")
    @PositiveOrZero(message = "Precio debe ser mayor o igual a cero")
    private Integer price;
    
    @NotNull(message = "Estado activo/inactivo es obligatorio")
    private Boolean active;
    
    @NotBlank(message = "ID de categoría es obligatorio")
    private String categoryId;
}
