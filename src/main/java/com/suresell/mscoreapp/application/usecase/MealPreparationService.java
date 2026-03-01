package com.suresell.mscoreapp.application.usecase;

import com.suresell.mscoreapp.application.usecase.ManageMealPreparationUseCase;
import com.suresell.mscoreapp.application.dto.MealPreparation;
import com.suresell.mscoreapp.application.dto.CreateMealPreparationRequest;
import com.suresell.mscoreapp.application.dto.UpdateMealPreparationRequest;
import com.suresell.mscoreapp.application.dto.WeeklyMealPlanResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class MealPreparationService {

    private static final Logger logger = LoggerFactory.getLogger(MealPreparationService.class);

    private final ManageMealPreparationUseCase useCase;

    public MealPreparationService(ManageMealPreparationUseCase useCase) {
        this.useCase = useCase;
    }

    public MealPreparation createMealPreparation(CreateMealPreparationRequest request) {
        logger.info("🍽️ Creando nueva preparación para {}", request.getPreparationDate());

        validateCreateRequest(request);
        return useCase.createMealPreparation(request);
    }

    public WeeklyMealPlanResponse getAllWeekPlan(int offset) {
        logger.debug("Obteniendo plan de preparaciones semana anterior, actual y siguiente");
        return useCase.getAllWeekPlan(offset);
    }

    public WeeklyMealPlanResponse getWeekPlan(LocalDate weekStartDate) {
        logger.debug("Obteniendo plan de preparaciones para semana: {}", weekStartDate);

        if (weekStartDate == null) {
            throw new IllegalArgumentException("Fecha de inicio de semana es requerida");
        }

        return useCase.getWeekPlan(weekStartDate);
    }

    public MealPreparation updateMealPreparation(String id, UpdateMealPreparationRequest request) {
        logger.info("🔄 Actualizando preparación: {}", id);

        validateUpdateRequest(request);
        return useCase.updateMealPreparation(id, request);
    }

    public void startPreparation(String id) {
        logger.info("Iniciando preparación: {}", id);
        useCase.startPreparation(id);
    }

    public void completePreparation(String id) {
        logger.info("Marcando preparación como completada: {}", id);
        useCase.completePreparation(id);
    }

    public void cancelPreparation(String id) {
        logger.info("Cancelando preparación: {}", id);
        useCase.cancelPreparation(id);
    }

    public void deleteMealPreparation(String id) {
        logger.info("Eliminando preparación: {}", id);
        useCase.deleteMealPreparation(id);
    }

    private void validateCreateRequest(CreateMealPreparationRequest request) {
        if (request.getPreparationDate() == null) {
            throw new IllegalArgumentException("Fecha de preparación es requerida");
        }

        if (request.getMainDish() == null || request.getMainDish().trim().isEmpty()) {
            throw new IllegalArgumentException("Plato principal es requerido");
        }

        if (request.getEstimatedPortions() != null && request.getEstimatedPortions() <= 0) {
            throw new IllegalArgumentException("Porciones estimadas debe ser un número positivo");
        }

        // Validar que la fecha no sea muy en el pasado
        if (request.getPreparationDate().isBefore(LocalDate.now().minusDays(7))) {
            throw new IllegalArgumentException("No se puede crear preparaciones para fechas muy pasadas");
        }
    }

    private void validateUpdateRequest(UpdateMealPreparationRequest request) {
        if (request.getMainDish() != null && request.getMainDish().trim().isEmpty()) {
            throw new IllegalArgumentException("Plato principal no puede estar vacío");
        }

        if (request.getEstimatedPortions() != null && request.getEstimatedPortions() <= 0) {
            throw new IllegalArgumentException("Porciones estimadas debe ser un número positivo");
        }
    }
}
