package com.suresell.mscoreapp.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WaiterClosurePreviewResponse {
    private String waiterId;
    private LocalDateTime previewTime;
    private BigDecimal totalExpectedCash;
    private BigDecimal totalExpectedCard;
    private BigDecimal totalExpectedQr;
    private BigDecimal totalExpected;
    private BigDecimal lastBaseCash;
    private String message;
}
