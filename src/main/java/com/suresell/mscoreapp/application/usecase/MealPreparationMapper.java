package com.suresell.mscoreapp.application.usecase;

import com.suresell.mscoreapp.application.dto.MealPreparation;
import com.suresell.mscoreapp.application.dto.CreateMealPreparationRequest;
import com.suresell.mscoreapp.application.dto.WeeklyMealPlanResponse;
import com.suresell.mscoreapp.application.dto.MealPreparationDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface MealPreparationMapper {

    // Domain to DTO
    @Mapping(target = "dayOfWeek", source = "dayOfWeek.spanishName")
    MealPreparationDto toDto(MealPreparation meal);

    List<MealPreparationDto> toDto(List<MealPreparation> meals);

    // Request to Domain (para crear nuevos)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "dayOfWeek", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    MealPreparation toDomain(CreateMealPreparationRequest request);

    // Método personalizado para response semanal
    default WeeklyMealPlanResponse toWeeklyResponse(List<MealPreparation> meals, LocalDate referenceDate) {
        // Calcular inicio de semana (lunes)
        LocalDate weekStart = referenceDate.with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
        LocalDate weekEnd = weekStart.plusDays(6);

        List<MealPreparationDto> mealDtos = toDto(meals);

        WeeklyMealPlanResponse response = new WeeklyMealPlanResponse();
        response.setWeekStartDate(weekStart);
        response.setWeekEndDate(weekEnd);
        response.setMeals(mealDtos);
        response.setTotalMeals(mealDtos.size());

        return response;
    }
}
