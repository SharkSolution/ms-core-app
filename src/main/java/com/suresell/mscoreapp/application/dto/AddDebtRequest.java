package com.suresell.mscoreapp.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddDebtRequest {

    @NotBlank(message = "Documento del cliente es requerido")
    private String customerDocument;

    @Positive(message = "Monto debe ser positivo")
    private BigDecimal amount;

    @NotBlank(message = "Descripción es requerida")
    private String description;

    private String reference; // Número de orden, factura, etc.
}
