package com.suresell.mscoreapp.application.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class UpdateQuantityRequest {
    private BigDecimal quantity;
}