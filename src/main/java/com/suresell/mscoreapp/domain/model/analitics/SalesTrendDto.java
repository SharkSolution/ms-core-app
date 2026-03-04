package com.suresell.mscoreapp.domain.model.analitics;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDate;

public record SalesTrendDto(
        @JsonProperty("fecha") LocalDate date,
        @JsonProperty("total_ventas") BigDecimal totalSales
) {}
