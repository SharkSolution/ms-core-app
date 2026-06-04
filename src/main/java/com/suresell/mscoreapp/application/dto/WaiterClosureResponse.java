package com.suresell.mscoreapp.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WaiterClosureResponse {
    private UUID id;
    private String waiterId;
    private String waiterName;
    private LocalDateTime closedAt;
    private BigDecimal baseCash;
    private BigDecimal totalExpectedCash;
    private BigDecimal totalExpectedCard;
    private BigDecimal totalExpectedQr;
    private BigDecimal totalCountedCash;
    private BigDecimal totalCountedCard;
    private BigDecimal totalCountedQr;
    private BigDecimal differenceCash;
    private BigDecimal differenceCard;
    private BigDecimal differenceQr;
    private BigDecimal totalDifference;
    private String status;
    private String notes;
    private String message;
}
