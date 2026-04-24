package com.suresell.mscoreapp.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QrPaymentDto {
    private Long id;
    private LocalDate paymentDate;
    private BigDecimal amount;
    private String notes;
    private String registeredBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
