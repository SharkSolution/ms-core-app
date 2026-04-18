package com.suresell.mscoreapp.infrastructure.web.adapter;

import com.suresell.mscoreapp.application.dto.ApproveRejectExpenseRequest;
import com.suresell.mscoreapp.application.dto.CreateExpenseRequest;
import com.suresell.mscoreapp.application.dto.ExpenseDto;
import com.suresell.mscoreapp.application.dto.ExpenseStatsResponse;
import com.suresell.mscoreapp.application.usecase.ManageExpenseUseCase;
import com.suresell.mscoreapp.shared.enums.ExpenseStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/expenses")
@Tag(name = "Gastos", description = "Gestión de gastos y control de aprobaciones")
public class ExpenseController {

    private final ManageExpenseUseCase useCase;

    public ExpenseController(ManageExpenseUseCase useCase) {
        this.useCase = useCase;
    }

    @GetMapping
    @Operation(summary = "Listar gastos", description = "Obtiene todos los gastos con filtros opcionales")
    public ResponseEntity<?> getAllExpenses(
            @RequestParam(required = false) ExpenseStatus status,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            List<ExpenseDto> expenses;

            if (status != null) {
                expenses = useCase.getExpensesByStatus(status);
            } else if (categoryId != null) {
                expenses = useCase.getExpensesByCategory(categoryId);
            } else if (startDate != null && endDate != null) {
                expenses = useCase.getExpensesByDateRange(startDate, endDate);
            } else {
                expenses = useCase.getAllExpenses();
            }

            return ResponseEntity.ok(expenses);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener gasto", description = "Obtiene un gasto por su ID")
    public ResponseEntity<?> getExpenseById(@PathVariable String id) {
        try {
            ExpenseDto expense = useCase.getExpenseById(id);
            return ResponseEntity.ok(expense);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping
    @Operation(summary = "Registrar gasto", description = "Crea un nuevo gasto en estado pendiente")
    public ResponseEntity<?> createExpense(@RequestBody CreateExpenseRequest request) {
        try {
            ExpenseDto created = useCase.createExpense(request);
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
    @Operation(summary = "Actualizar gasto", description = "Actualiza un gasto (solo si está en estado pendiente)")
    public ResponseEntity<?> updateExpense(@PathVariable String id, @RequestBody CreateExpenseRequest request) {
        try {
            ExpenseDto updated = useCase.updateExpense(id, request);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}/approve")
    @Operation(summary = "Aprobar gasto", description = "Aprueba un gasto pendiente")
    public ResponseEntity<?> approveExpense(@PathVariable String id, @RequestBody ApproveRejectExpenseRequest request) {
        try {
            ExpenseDto approved = useCase.approveExpense(id, request);
            return ResponseEntity.ok(approved);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}/reject")
    @Operation(summary = "Rechazar gasto", description = "Rechaza un gasto pendiente con una razón")
    public ResponseEntity<?> rejectExpense(@PathVariable String id, @RequestBody ApproveRejectExpenseRequest request) {
        try {
            ExpenseDto rejected = useCase.rejectExpense(id, request);
            return ResponseEntity.ok(rejected);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar gasto", description = "Elimina un gasto (solo si está en estado pendiente)")
    public ResponseEntity<?> deleteExpense(@PathVariable String id) {
        try {
            useCase.deleteExpense(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/stats")
    @Operation(summary = "Estadísticas de gastos", description = "Obtiene estadísticas generales de gastos")
    public ResponseEntity<?> getExpenseStats() {
        try {
            ExpenseStatsResponse stats = useCase.getExpenseStats();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
