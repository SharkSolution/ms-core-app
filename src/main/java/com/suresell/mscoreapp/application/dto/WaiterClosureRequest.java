package com.suresell.mscoreapp.application.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WaiterClosureRequest {
    @NotBlank(message = "El ID del mesero es obligatorio")
    private String waiterId;

    @NotBlank(message = "El nombre del mesero es obligatorio")
    private String waiterName;

    @NotNull(message = "La base en efectivo es obligatoria")
    @DecimalMin(value = "0.0", inclusive = true, message = "La base debe ser mayor o igual a 0")
    private BigDecimal baseCash;

    @NotNull(message = "El total contado en efectivo es obligatorio")
    @DecimalMin(value = "0.0", inclusive = true, message = "El total en efectivo debe ser mayor o igual a 0")
    private BigDecimal totalCountedCash;

    @NotNull(message = "El total contado en tarjeta es obligatorio")
    @DecimalMin(value = "0.0", inclusive = true, message = "El total en tarjeta debe ser mayor o igual a 0")
    private BigDecimal totalCountedCard;

    @NotNull(message = "El total contado en QR es obligatorio")
    @DecimalMin(value = "0.0", inclusive = true, message = "El total en QR debe ser mayor o igual a 0")
    private BigDecimal totalCountedQr;

    private String notes;
}
