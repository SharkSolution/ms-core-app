package com.suresell.mscoreapp.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.suresell.mscoreapp.application.dto.MealPreparationDto;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WeeklyMealPlanResponse {
    private LocalDate weekStartDate;
    private LocalDate weekEndDate;
    private List<MealPreparationDto> meals;
    private int totalMeals;
    private int completedMeals;
}
