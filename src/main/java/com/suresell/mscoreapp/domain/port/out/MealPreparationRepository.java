package com.suresell.mscoreapp.domain.port.out;

import com.suresell.mscoreapp.application.dto.MealPreparation;
import com.suresell.mscoreapp.application.dto.DayOfWeek;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MealPreparationRepository {
    List<MealPreparation> findAllMealPreparations();
    Optional<MealPreparation> findById(String id);
    List<MealPreparation> findByWeek(LocalDate startOfWeek);
    List<MealPreparation> findByDateRange(LocalDate startDate, LocalDate endDate);
    Optional<MealPreparation> findByDate(LocalDate date);
    List<MealPreparation> findByDayOfWeek(DayOfWeek dayOfWeek);
    MealPreparation save(MealPreparation mealPreparation);
    void deleteById(String id);
    List<MealPreparation> findByWeekOffset(int offset);
}
