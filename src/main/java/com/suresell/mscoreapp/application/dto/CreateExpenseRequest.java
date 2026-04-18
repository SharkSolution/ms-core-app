package com.suresell.mscoreapp.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateExpenseRequest {
    @NotNull(message = "El monto es requerido")
    @Positive(message = "El monto debe ser positivo")
    private BigDecimal amount;

    @Positive(message = "La cantidad debe ser positiva")
    private Double quantity;

    @Positive(message = "El precio unitario debe ser positivo")
    private BigDecimal unitPrice;

    @NotBlank(message = "La descripción es requerida")
    private String description;

    private String vendor;

    private String imageUrl;

    @NotNull(message = "La categoría es requerida")
    private Long categoryId;

    @NotNull(message = "La fecha del gasto es requerida")
    private LocalDate expenseDate;

    @NotBlank(message = "El usuario creador es requerido")
    private String createdBy;

    private String notes;
}
