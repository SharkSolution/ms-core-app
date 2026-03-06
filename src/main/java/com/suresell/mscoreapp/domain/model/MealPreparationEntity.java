package com.suresell.mscoreapp.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.suresell.mscoreapp.application.dto.DayOfWeek;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "meal_preparations",
        indexes = {
                @Index(name = "idx_meal_prep_date", columnList = "preparation_date"),
                @Index(name = "idx_meal_prep_week", columnList = "preparation_date, day_of_week")
        })
public class MealPreparationEntity {

    @Id
    @Column(length = 36)
    private String id;

    @Column(name = "preparation_date", nullable = false)
    private LocalDate preparationDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week", nullable = false)
    private DayOfWeek dayOfWeek;

    @Column(name = "main_dish", nullable = false, length = 200)
    private String mainDish;

    @Column(name = "side_dish", length = 200)
    private String sideDish;

    @Column(name = "soup", length = 200)
    private String soup;

    @Column(name = "beverage", length = 100)
    private String beverage;

    @Column(name = "dessert", length = 200)
    private String dessert;

    @Column(name = "special_notes", length = 500)
    private String specialNotes;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        if (this.createdAt == null) {
            this.createdAt = now;
        }
        if (this.updatedAt == null) {
            this.updatedAt = now;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
