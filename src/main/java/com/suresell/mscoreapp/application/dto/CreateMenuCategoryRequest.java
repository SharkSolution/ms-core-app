package com.suresell.mscoreapp.application.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateMenuCategoryRequest {
    @NotBlank(message = "ID de categoría es obligatorio")
    private String id;
    
    @NotBlank(message = "Nombre de categoría es obligatorio")
    private String name;
}
