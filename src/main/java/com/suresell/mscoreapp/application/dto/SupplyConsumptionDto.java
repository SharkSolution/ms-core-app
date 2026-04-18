package com.suresell.mscoreapp.application.dto;

import com.suresell.mscoreapp.shared.enums.Reason;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
}
