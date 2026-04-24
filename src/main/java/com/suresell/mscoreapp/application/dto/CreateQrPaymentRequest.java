package com.suresell.mscoreapp.application.dto;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateQrPaymentRequest(
        @NotNull(message = "La fecha no puede ser nula")
        LocalDate paymentDate,
        
        @NotNull(message = "El monto no puede ser nulo")
        BigDecimal amount,
        
        String notes,
        
        String registeredBy
) {}
