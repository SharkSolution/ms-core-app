package com.suresell.mscoreapp.application.usecase;

import com.suresell.mscoreapp.application.usecase.MealPreparationMapper;
import com.suresell.mscoreapp.application.dto.MealPreparation;
import com.suresell.mscoreapp.domain.port.out.MealPreparationRepository;
import com.suresell.mscoreapp.application.dto.CreateMealPreparationRequest;
import com.suresell.mscoreapp.application.dto.UpdateMealPreparationRequest;
import com.suresell.mscoreapp.application.dto.WeeklyMealPlanResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Component
public class ManageMealPreparationUseCase {

    private static final Logger logger = LoggerFactory.getLogger(ManageMealPreparationUseCase.class);

    private final MealPreparationRepository repository;
    private final MealPreparationMapper mapper;

    public ManageMealPreparationUseCase(MealPreparationRepository repository, MealPreparationMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Transactional
    public MealPreparation createMealPreparation(CreateMealPreparationRequest request) {
        logger.info("📝 Creando preparación para {}: {}", request.getPreparationDate(), request.getMainDish());

        // Verificar si ya existe una preparación para esa fecha
        repository.findByDate(request.getPreparationDate()).ifPresent(existing -> {
            throw new IllegalArgumentException("Ya existe una preparación para la fecha: " + request.getPreparationDate());
        });

        MealPreparation meal = MealPreparation.createNew(
                request.getPreparationDate(),
                request.getMainDish(),
                request.getSideDish()
        );

        // Configurar campos opcionales
        meal.setSoup(request.getSoup());
        meal.setBeverage(request.getBeverage());
        meal.setDessert(request.getDessert());
        meal.setSpecialNotes(request.getSpecialNotes());

        return repository.save(meal);
    }

    public WeeklyMealPlanResponse getAllWeekPlan(int offset) {
        logger.debug("📅 Obteniendo plan semana con offset: {}", offset);

        List<MealPreparation> meals = repository.findByWeekOffset(offset);
        LocalDate targetWeek = LocalDate.now().plusWeeks(offset);
        return mapper.toWeeklyResponse(meals, targetWeek);
    }

    public WeeklyMealPlanResponse getWeekPlan(LocalDate weekStartDate) {
        logger.debug("📅 Obteniendo plan para semana: {}", weekStartDate);

        List<MealPreparation> meals = repository.findByWeek(weekStartDate);
        return mapper.toWeeklyResponse(meals, weekStartDate);
    }

    @Transactional
    public MealPreparation updateMealPreparation(String id, UpdateMealPreparationRequest request) {
        logger.info("🔄 Actualizando preparación: {}", id);

        MealPreparation meal = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Preparación no encontrada: " + id));

        meal.updateMeal(
                request.getMainDish(),
                request.getSideDish(),
                request.getSoup(),
                request.getBeverage(),
                request.getDessert(),
                request.getSpecialNotes()
        );

        return repository.save(meal);
    }

    @Transactional
    public void startPreparation(String id) {
        logger.info("🚀 Iniciando preparación: {}", id);

        MealPreparation meal = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Preparación no encontrada: " + id));

        meal.markAsInProgress();
        repository.save(meal);
    }

    @Transactional
    public void completePreparation(String id) {
        logger.info("✅ Completando preparación: {}", id);

        MealPreparation meal = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Preparación no encontrada: " + id));

        meal.markAsCompleted();
        repository.save(meal);
    }

    @Transactional
    public void cancelPreparation(String id) {
        logger.info("❌ Cancelando preparación: {}", id);

        MealPreparation meal = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Preparación no encontrada: " + id));

        meal.cancel();
        repository.save(meal);
    }

    @Transactional
    public void deleteMealPreparation(String id) {
        logger.info("🗑️ Eliminando preparación: {}", id);
        repository.deleteById(id);
    }

}
