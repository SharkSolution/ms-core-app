package com.suresell.mscoreapp.application.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateAccountRequest {

    @NotBlank(message = "Nombre del cliente es requerido")
    private String customerName;

    @NotBlank(message = "Documento del cliente es requerido")
    private String customerDocument;

    private String customerPhone;

    @DecimalMin(value = "0.0", message = "Límite de crédito no puede ser negativo")
    private BigDecimal creditLimit;

    private String notes;
}
