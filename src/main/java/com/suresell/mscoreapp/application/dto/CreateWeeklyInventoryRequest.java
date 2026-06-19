package com.suresell.mscoreapp.application.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class CreateWeeklyInventoryRequest {
    // Opcional: si no se envía, se usa el lunes de la semana actual.
    private LocalDate weekStart;
    // Opcional: origen del conteo (por defecto KITCHEN).
    private String source;
    private String createdBy;
    private List<WeeklyInventoryItemRequest> items;
}
