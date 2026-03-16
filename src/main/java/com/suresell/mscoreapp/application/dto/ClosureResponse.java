package com.suresell.mscoreapp.application.dto;

import com.suresell.mscoreapp.shared.enums.ClosureStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClosureResponse {
    private UUID id;
    private String userName;
    private LocalDateTime openingTime;
    private LocalDateTime closingTime;
    
    // Totales Esperados
    private BigDecimal totalExpectedCash;
    private BigDecimal totalExpectedCard;
    private BigDecimal totalExpectedNequi;
    private BigDecimal totalExpectedQr;
    
    // Totales Contados
    private BigDecimal totalCountedCash;
    private BigDecimal totalCountedCard;
        private BigDecimal totalCountedNequi;
        private BigDecimal totalCountedQr;
    
        // Totales Consolidados
        private BigDecimal totalExpected;
        private BigDecimal totalCounted;
    
        private BigDecimal differenceAmount;
        private ClosureStatus status;
    
    private String notes;
    private BigDecimal baseBalanceForNextDay;
}
