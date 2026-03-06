package com.suresell.mscoreapp.infrastructure.persistence;

import com.suresell.mscoreapp.application.usecase.MealPreparationEntityMapper;
import com.suresell.mscoreapp.application.dto.MealPreparation;
import com.suresell.mscoreapp.domain.model.MealPreparationEntity;
import com.suresell.mscoreapp.domain.port.out.MealPreparationRepository;
import com.suresell.mscoreapp.infrastructure.persistence.jpa.MealPreparationPanacheRepository;
import com.suresell.mscoreapp.application.dto.DayOfWeek;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class MealPreparationRepositoryImpl implements MealPreparationRepository {

    private static final Logger logger = LoggerFactory.getLogger(MealPreparationRepositoryImpl.class);

    private final MealPreparationEntityMapper mapper;
    private final MealPreparationPanacheRepository mealPreparationJpaRepository;

    public MealPreparationRepositoryImpl(MealPreparationEntityMapper mapper,
                                         MealPreparationPanacheRepository mealPreparationJpaRepository) {
        this.mapper = mapper;
        this.mealPreparationJpaRepository = mealPreparationJpaRepository;
    }

    @Override
    public List<MealPreparation> findAllMealPreparations() {
        logger.debug("Obteniendo todas las preparaciones");
        return mealPreparationJpaRepository.findAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<MealPreparation> findById(String id) {
        logger.debug("Buscando preparación por ID: {}", id);
        return mealPreparationJpaRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public List<MealPreparation> findByWeek(LocalDate startOfWeek) {
        LocalDate endOfWeek = startOfWeek.plusDays(6);
        logger.debug("Buscando preparaciones para la semana: {} - {}", startOfWeek, endOfWeek);
        return mealPreparationJpaRepository.findByPreparationDateBetweenOrderByPreparationDate(startOfWeek, endOfWeek).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<MealPreparation> findByDateRange(LocalDate startDate, LocalDate endDate) {
        logger.debug("Buscando preparaciones en rango: {} - {}", startDate, endDate);
        return mealPreparationJpaRepository.findByPreparationDateBetweenOrderByPreparationDate(startDate, endDate).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<MealPreparation> findByDate(LocalDate date) {
        logger.debug("Buscando preparación para fecha: {}", date);
        return mealPreparationJpaRepository.findByPreparationDate(date)
                .map(mapper::toDomain);
    }

    @Override
    public List<MealPreparation> findByDayOfWeek(DayOfWeek dayOfWeek) {
        return mealPreparationJpaRepository.findByDayOfWeek(dayOfWeek).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public MealPreparation save(MealPreparation mealPreparation) {
        logger.debug("Guardando preparación: {} para {}",
                mealPreparation.getMainDish(), mealPreparation.getPreparationDate());

        if (mealPreparation.getId() != null) {
            return updateExistingMeal(mealPreparation);
        } else {
            return createNewMeal(mealPreparation);
        }
    }

    private MealPreparation updateExistingMeal(MealPreparation meal) {
        logger.debug("Actualizando preparación existente: {}", meal.getId());
        MealPreparationEntity existingEntity = mealPreparationJpaRepository.findById(meal.getId())
                .orElseThrow(() -> new IllegalArgumentException("Meal preparation not found: " + meal.getId()));

        mapper.updateEntity(existingEntity, meal);
        mealPreparationJpaRepository.save(existingEntity);
        logger.info("Preparación actualizada: {} para {}", meal.getMainDish(), meal.getPreparationDate());
        return mapper.toDomain(existingEntity);
    }

    private MealPreparation createNewMeal(MealPreparation meal) {
        logger.debug("Creando nueva preparación para: {}", meal.getPreparationDate());
        meal.setId(UUID.randomUUID().toString());
        MealPreparationEntity newEntity = mapper.toEntity(meal);
        mealPreparationJpaRepository.save(newEntity);
        logger.info("Nueva preparación creada: {} para {}", meal.getMainDish(), meal.getPreparationDate());
        return mapper.toDomain(newEntity);
    }

    @Override
    @Transactional
    public void deleteById(String id) {
        logger.debug("Eliminando preparación: {}", id);
        mealPreparationJpaRepository.deleteById(id);
        logger.info("Preparación {} eliminada", id);
    }

    @Override
    public List<MealPreparation> findByWeekOffset(int offset) {
        LocalDate today = LocalDate.now();
        LocalDate targetDate = today.plusWeeks(offset);
        LocalDate startOfWeek = targetDate.with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
        logger.debug("Buscando preparaciones semana offset {}: {}", offset, startOfWeek);
        return findByWeek(startOfWeek);
    }
}
