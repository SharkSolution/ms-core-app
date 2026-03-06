package com.suresell.mscoreapp.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MealPreparationDto {
    private String id;
    private LocalDate preparationDate;
    private String dayOfWeek;
    private String mainDish;
    private String sideDish;
    private String soup;
    private String beverage;
    private String dessert;
    private String specialNotes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
