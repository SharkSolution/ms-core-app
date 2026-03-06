package com.suresell.mscoreapp.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateMealPreparationRequest {

    @NotNull(message = "Fecha de preparación es requerida")
    private LocalDate preparationDate;

    @NotBlank(message = "Plato principal es requerido")
    private String mainDish;

    private String sideDish;
    private String soup;
    private String beverage;
    private String dessert;

    @Positive(message = "Porciones estimadas debe ser positivo")
    private Integer estimatedPortions;

    private String specialNotes;
}
