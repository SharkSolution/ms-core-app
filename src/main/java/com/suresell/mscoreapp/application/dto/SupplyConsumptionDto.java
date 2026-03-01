package com.suresell.mscoreapp.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.suresell.mscoreapp.shared.enums.Reason;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SupplyConsumptionDto {

    private Long id;
    private Long supplyId;
    private BigDecimal quantity;
    private Reason reason;
    private LocalDateTime registrationDate;
    private String registeredBy;
    private String observations;
}
