package com.suresell.mscoreapp.infrastructure.web.adapter;

import com.suresell.mscoreapp.application.usecase.SupplyService;
import com.suresell.mscoreapp.application.dto.CreateSupplyDto;
import com.suresell.mscoreapp.application.dto.SupplyDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/supplies")
@Tag(name = "Gestión de Insumos (Materia Prima)", description = "Endpoints para el maestro de materia prima y control de inventario base")
public class SupplyController {

    private final SupplyService supplyService;

    public SupplyController(SupplyService supplyService) {
        this.supplyService = supplyService;
    }

    @PostMapping
    @Operation(summary = "Crear nuevo insumo", description = "Registra una nueva materia prima en el maestro de insumos")
    public ResponseEntity<?> createSupply(@RequestBody CreateSupplyDto dto) {
        try {
            supplyService.createSupply(dto);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping
    @Operation(summary = "Listar todos los insumos", description = "Obtiene el maestro completo de materia prima con sus niveles de stock")
    public ResponseEntity<?> getAllSupplies() {
        try {
            List<SupplyDto> supplies = supplyService.getAllSupplies();
            return ResponseEntity.ok(supplies);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/category/{categoryId}")
    @Operation(summary = "Filtrar por categoría", description = "Obtiene los insumos pertenecientes a una categoría específica")
    public ResponseEntity<?> getSuppliesByCategoryId(@PathVariable("categoryId") Long categoryId) {
        try {
            List<SupplyDto> supplies = supplyService.getSuppliesByCategoryId(categoryId);
            return ResponseEntity.ok(supplies);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Supply Category not found"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
