package com.suresell.mscoreapp.infrastructure.web.adapter;

import jakarta.validation.Valid;
import com.suresell.mscoreapp.application.usecase.MealPreparationService;
import com.suresell.mscoreapp.application.dto.MealPreparation;
import com.suresell.mscoreapp.application.dto.CreateMealPreparationRequest;
import com.suresell.mscoreapp.application.dto.UpdateMealPreparationRequest;
import com.suresell.mscoreapp.application.dto.WeeklyMealPlanResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/meal-preparations")
public class MealPreparationController {

    private static final Logger logger = LoggerFactory.getLogger(MealPreparationController.class);

    private final MealPreparationService service;

    public MealPreparationController(MealPreparationService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<?> createMealPreparation(@Valid @RequestBody CreateMealPreparationRequest request) {
        logger.info("🍽️ POST /api/meal-preparations - Creando preparación para {}", request.getPreparationDate());

        try {
            MealPreparation meal = service.createMealPreparation(request);
            logger.info("Preparación creada exitosamente: {}", meal.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(meal);
        } catch (IllegalArgumentException e) {
            logger.warn("⚠Datos inválidos para crear preparación: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error inesperado creando preparación", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/week/general/{offset}")
    public ResponseEntity<?> getWeekPlan(@PathVariable("offset") int offset) {
        logger.info("GET /api/meal-preparations/week/{} - Obteniendo plan semana offset", offset);

        try {
            WeeklyMealPlanResponse plan = service.getAllWeekPlan(offset);
            logger.info("Plan semana offset {} obtenido: {} preparaciones", offset, plan.getTotalMeals());
            return ResponseEntity.ok(plan);
        } catch (Exception e) {
            logger.error("Error obteniendo plan semana offset: {}", offset, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/week/{date}")
    public ResponseEntity<?> getWeekPlanByDate(@PathVariable("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate weekStartDate) {
        logger.info("GET /api/meal-preparations/week/{} - Obteniendo plan para fecha", weekStartDate);

        try {
            WeeklyMealPlanResponse plan = service.getWeekPlan(weekStartDate);
            logger.info("Plan semana {} obtenido: {} preparaciones", weekStartDate, plan.getTotalMeals());
            return ResponseEntity.ok(plan);
        } catch (Exception e) {
            logger.error("Error obteniendo plan para semana: {}", weekStartDate, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Fecha inválida o error procesando: " + weekStartDate));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateMealPreparation(@PathVariable("id") String id,
                                                   @Valid @RequestBody UpdateMealPreparationRequest request) {
        logger.info("🔄 PUT /api/meal-preparations/{} - Actualizando preparación", id);

        try {
            MealPreparation meal = service.updateMealPreparation(id, request);
            logger.info("Preparación {} actualizada exitosamente", id);
            return ResponseEntity.ok(meal);
        } catch (IllegalArgumentException e) {
            logger.warn("⚠Error actualizando preparación {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error inesperado actualizando preparación: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}/start")
    public ResponseEntity<?> startPreparation(@PathVariable("id") String id) {
        logger.info("PUT /api/meal-preparations/{}/start - Iniciando preparación", id);

        try {
            service.startPreparation(id);
            logger.info("Preparación {} iniciada exitosamente", id);
            return ResponseEntity.ok(Map.of("message", "Preparación iniciada"));
        } catch (IllegalArgumentException e) {
            logger.warn("Error iniciando preparación {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error inesperado iniciando preparación: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<?> completePreparation(@PathVariable("id") String id) {
        logger.info("PUT /api/meal-preparations/{}/complete - Completando preparación", id);

        try {
            service.completePreparation(id);
            logger.info("Preparación {} completada exitosamente", id);
            return ResponseEntity.ok(Map.of("message", "Preparación completada"));
        } catch (IllegalArgumentException e) {
            logger.warn("Error completando preparación {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error inesperado completando preparación: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<?> cancelPreparation(@PathVariable("id") String id) {
        logger.info("PUT /api/meal-preparations/{}/cancel - Cancelando preparación", id);

        try {
            service.cancelPreparation(id);
            logger.info("Preparación {} cancelada exitosamente", id);
            return ResponseEntity.ok(Map.of("message", "Preparación cancelada"));
        } catch (IllegalArgumentException e) {
            logger.warn("⚠Error cancelando preparación {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error inesperado cancelando preparación: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMealPreparation(@PathVariable("id") String id) {
        logger.info("🗑️ DELETE /api/meal-preparations/{} - Eliminando preparación", id);

        try {
            service.deleteMealPreparation(id);
            logger.info("Preparación {} eliminada exitosamente", id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            logger.warn("Error eliminando preparación {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error inesperado eliminando preparación: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
