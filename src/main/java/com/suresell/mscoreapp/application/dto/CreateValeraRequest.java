package com.suresell.mscoreapp.application.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateValeraRequest {

    @NotBlank(message = "Nombre del cliente es requerido")
    private String customerName;

    @NotBlank(message = "Documento del cliente es requerido")
    private String customerDocument;

    private String customerPhone;

    @NotNull(message = "Tipo de valera es requerido")
    private String type; // ValeraType as String

    @Positive(message = "Total de comidas debe ser positivo")
    @Max(value = 365, message = "Máximo 365 comidas por valera")
    private Integer totalMeals;

    @Positive(message = "Precio unitario debe ser positivo")
    private BigDecimal unitPrice;

    @Min(value = 1, message = "Mínimo 1 día de validez")
    @Max(value = 365, message = "Máximo 365 días de validez")
    private Integer validityDays;

    @DecimalMin(value = "0.0", message = "Descuento no puede ser negativo")
    @DecimalMax(value = "100.0", message = "Descuento no puede ser mayor a 100%")
    private BigDecimal discountPercentage;

    private String notes;
}

