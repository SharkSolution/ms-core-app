package com.suresell.mscoreapp.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WaiterDto {
    private Long id;
    private String name;
    private Boolean active;
    private BigDecimal dailySaleGoal;
    private BigDecimal commissionPercentage;
}
