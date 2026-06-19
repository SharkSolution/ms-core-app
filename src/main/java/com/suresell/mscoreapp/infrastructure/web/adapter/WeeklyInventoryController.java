package com.suresell.mscoreapp.infrastructure.web.adapter;

import com.suresell.mscoreapp.application.dto.CreateWeeklyInventoryRequest;
import com.suresell.mscoreapp.application.usecase.ManageWeeklyInventoryUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/weekly-inventory")
@Tag(name = "Inventario Semanal", description = "Conteo semanal de existencias (cocina) sincronizado con el inventario del Admin")
public class WeeklyInventoryController {

    private final ManageWeeklyInventoryUseCase useCase;

    public WeeklyInventoryController(ManageWeeklyInventoryUseCase useCase) {
        this.useCase = useCase;
    }

    @PostMapping
    @Operation(summary = "Registrar conteo semanal", description = "Cocina registra existencias; fija el stock de cada insumo en el Admin")
    public ResponseEntity<?> register(@RequestBody CreateWeeklyInventoryRequest request) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(useCase.register(request));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping
    @Operation(summary = "Listar conteos semanales", description = "Histórico de conteos; filtra por week_start (lunes de la semana) si se envía")
    public ResponseEntity<?> list(
            @RequestParam(value = "weekStart", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate weekStart) {
        try {
            return ResponseEntity.ok(useCase.list(weekStart));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }
}
