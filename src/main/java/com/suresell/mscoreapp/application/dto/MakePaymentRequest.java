package com.suresell.mscoreapp.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class MakePaymentRequest {

    @NotBlank(message = "Documento del cliente es requerido")
    private String customerDocument;

    @Positive(message = "Monto del pago debe ser positivo")
    private BigDecimal amount;

    private String description;

    @NotNull(message = "Método de pago es requerido")
    private String paymentMethod;
}
