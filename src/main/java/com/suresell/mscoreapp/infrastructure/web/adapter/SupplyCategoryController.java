package com.suresell.mscoreapp.infrastructure.web.adapter;

import com.suresell.mscoreapp.application.usecase.SupplyCategoryService;
import com.suresell.mscoreapp.application.dto.SupplyCategoryDto;
import com.suresell.mscoreapp.application.dto.CreateSupplyCategoryDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/supply-categories")
public class SupplyCategoryController {

    private final SupplyCategoryService supplyCategoryService;

    public SupplyCategoryController(SupplyCategoryService supplyCategoryService) {
        this.supplyCategoryService = supplyCategoryService;
    }

    @PostMapping
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
