package com.suresell.mscoreapp.infrastructure.persistence.jpa;

import com.suresell.mscoreapp.domain.model.MealPreparationEntity;
import com.suresell.mscoreapp.application.dto.DayOfWeek;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface MealPreparationPanacheRepository extends JpaRepository<MealPreparationEntity, String> {
    List<MealPreparationEntity> findByPreparationDateBetweenOrderByPreparationDate(LocalDate start, LocalDate end);
    Optional<MealPreparationEntity> findByPreparationDate(LocalDate date);
    List<MealPreparationEntity> findByDayOfWeek(DayOfWeek dayOfWeek);
}
