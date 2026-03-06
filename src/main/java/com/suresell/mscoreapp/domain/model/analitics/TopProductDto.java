package com.suresell.mscoreapp.domain.model.analitics;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public record TopProductDto(
        @JsonProperty("producto") String product,
        @JsonProperty("cantidad") Long quantity,
        @JsonProperty("ingresos") BigDecimal revenue
) {}
