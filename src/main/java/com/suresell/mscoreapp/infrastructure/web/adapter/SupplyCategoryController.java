package com.suresell.mscoreapp.infrastructure.web.adapter;

import com.suresell.mscoreapp.application.usecase.SupplyCategoryService;
import com.suresell.mscoreapp.application.dto.SupplyCategoryDto;
import com.suresell.mscoreapp.application.dto.CreateSupplyCategoryDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/supply-categories")
@Tag(name = "Categorías de Insumos", description = "Endpoints para organizar la materia prima por grupos")
public class SupplyCategoryController {

    private final SupplyCategoryService supplyCategoryService;

    public SupplyCategoryController(SupplyCategoryService supplyCategoryService) {
        this.supplyCategoryService = supplyCategoryService;
    }

    @PostMapping
    @Operation(summary = "Crear categoría", description = "Crea una nueva agrupación para los insumos")
    public ResponseEntity<?> createSupplyCategory(@RequestBody CreateSupplyCategoryDto dto) {
        try {
            supplyCategoryService.createSupplyCategory(dto);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping
    @Operation(summary = "Listar categorías", description = "Obtiene el listado completo de categorías disponibles")
    public ResponseEntity<?> getAllSupplyCategories() {
        try {
            List<SupplyCategoryDto> categories = supplyCategoryService.getAllSupplyCategories();
            return ResponseEntity.ok(categories);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
