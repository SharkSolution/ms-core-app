package com.suresell.mscoreapp.application.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateExpenseCategoryRequest {
    @NotBlank(message = "El nombre es requerido")
    private String name;
    private String description;
}
