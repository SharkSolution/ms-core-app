package com.suresell.mscoreapp.application.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import com.suresell.mscoreapp.application.dto.DayOfWeek;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class MealPreparation {
    private String id;
    private LocalDate preparationDate;
    private DayOfWeek dayOfWeek;
    private String mainDish;
    private String sideDish;
    private String soup;
    private String beverage;
    private String dessert;
    private String specialNotes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Factory method para crear nueva preparación
    public static MealPreparation createNew(LocalDate date, String mainDish,
                                            String sideDish) {
        MealPreparation meal = new MealPreparation();

        meal.setPreparationDate(date);
        meal.setDayOfWeek(DayOfWeek.fromLocalDate(date));
        meal.setMainDish(mainDish);
        meal.setSideDish(sideDish);
        meal.setCreatedAt(LocalDateTime.now());
        meal.setUpdatedAt(LocalDateTime.now());
        return meal;
    }

    public void updateMeal(String mainDish, String sideDish, String soup,
                           String beverage, String dessert, String notes) {
        this.mainDish = mainDish;
        this.sideDish = sideDish;
        this.soup = soup;
        this.beverage = beverage;
        this.dessert = dessert;
        this.specialNotes = notes;
        this.updatedAt = LocalDateTime.now();
    }

    public void markAsInProgress() {
        this.updatedAt = LocalDateTime.now();
    }

    public void markAsCompleted() {
        this.updatedAt = LocalDateTime.now();
    }

    public void cancel() {
        this.updatedAt = LocalDateTime.now();
    }
}
