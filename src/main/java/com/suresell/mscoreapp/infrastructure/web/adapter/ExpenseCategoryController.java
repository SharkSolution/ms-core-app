package com.suresell.mscoreapp.infrastructure.web.adapter;

import com.suresell.mscoreapp.application.dto.CreateExpenseCategoryRequest;
import com.suresell.mscoreapp.application.dto.ExpenseCategoryDto;
import com.suresell.mscoreapp.application.usecase.ExpenseCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/expense-categories")
@Tag(name = "Categorías de Gastos", description = "Gestión de categorías para clasificar gastos")
public class ExpenseCategoryController {

    private final ExpenseCategoryService service;

    public ExpenseCategoryController(ExpenseCategoryService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Listar categorías activas", description = "Obtiene todas las categorías de gastos activas")
    public ResponseEntity<?> getActiveCategories() {
        try {
            List<ExpenseCategoryDto> categories = service.getActiveCategories();
            return ResponseEntity.ok(categories);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/all")
    @Operation(summary = "Listar todas las categorías", description = "Obtiene todas las categorías incluyendo las inactivas")
    public ResponseEntity<?> getAllCategories() {
        try {
            List<ExpenseCategoryDto> categories = service.getAllCategories();
            return ResponseEntity.ok(categories);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener categoría", description = "Obtiene una categoría por su ID")
    public ResponseEntity<?> getCategoryById(@PathVariable Long id) {
        try {
            ExpenseCategoryDto category = service.getCategoryById(id);
            return ResponseEntity.ok(category);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping
    @Operation(summary = "Crear categoría", description = "Crea una nueva categoría de gastos")
    public ResponseEntity<?> createCategory(@RequestBody CreateExpenseCategoryRequest request) {
        try {
            ExpenseCategoryDto created = service.createCategory(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar categoría", description = "Actualiza una categoría existente")
    public ResponseEntity<?> updateCategory(@PathVariable Long id, @RequestBody CreateExpenseCategoryRequest request) {
        try {
            ExpenseCategoryDto updated = service.updateCategory(id, request);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Desactivar categoría", description = "Desactiva una categoría (soft delete)")
    public ResponseEntity<?> deactivateCategory(@PathVariable Long id) {
        try {
            service.deactivateCategory(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}/activate")
    @Operation(summary = "Activar categoría", description = "Reactiva una categoría desactivada")
    public ResponseEntity<?> activateCategory(@PathVariable Long id) {
        try {
            service.activateCategory(id);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
