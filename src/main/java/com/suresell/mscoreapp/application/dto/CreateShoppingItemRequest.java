package com.suresell.mscoreapp.application.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CreateShoppingItemRequest {
    private String name;
    private String supplyCategory;
    private String unit;
    private BigDecimal currentStock;
    private BigDecimal minimumStock;
}
