package com.suresell.mscoreapp.domain.model.analitics;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CashPerformanceDto(
        @JsonProperty("fecha") LocalDate date,
        @JsonProperty("diferencia") BigDecimal difference,
        @JsonProperty("usuario") String user
) {}
