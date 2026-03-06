package com.suresell.mscoreapp.application.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateWaiterRequest {
    @NotBlank(message = "El nombre del mesero es obligatorio")
    private String name;
}
