package com.suresell.mscoreapp.infrastructure.web.adapter;

import com.suresell.mscoreapp.application.dto.CreateMenuCategoryRequest;
import com.suresell.mscoreapp.application.dto.CreateMenuProductRequest;
import com.suresell.mscoreapp.application.dto.MenuCategoryDto;
import com.suresell.mscoreapp.application.dto.MenuProductDto;
import com.suresell.mscoreapp.application.usecase.ManageMenuUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/menu")
@RequiredArgsConstructor
@Tag(name = "Menu Management", description = "Operaciones para gestionar categorías y productos del menú")
public class MenuController {

    private final ManageMenuUseCase menuUseCase;

    // --- Categorías ---

    @GetMapping("/categories")
    @Operation(summary = "Obtener todas las categorías con paginación y ordenamiento")
    public Page<MenuCategoryDto> getAllCategories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "ASC") String direction) {
        
        Sort sort = Sort.by(Sort.Direction.fromString(direction.toUpperCase()), sortBy);
        return menuUseCase.getAllCategories(PageRequest.of(page, size, sort));
    }

    @PostMapping("/categories")
    @Operation(summary = "Crear o actualizar una categoría")
    public ResponseEntity<MenuCategoryDto> createCategory(@Valid @RequestBody CreateMenuCategoryRequest request) {
        return new ResponseEntity<>(menuUseCase.saveCategory(request), HttpStatus.CREATED);
    }

    @DeleteMapping("/categories/{id}")
    @Operation(summary = "Eliminar una categoría")
    public ResponseEntity<Void> deleteCategory(@PathVariable String id) {
        menuUseCase.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

    // --- Productos ---

    @GetMapping("/products")
    @Operation(summary = "Obtener todos los productos con paginación y ordenamiento")
    public Page<MenuProductDto> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "ASC") String direction) {
        
        Sort sort = Sort.by(Sort.Direction.fromString(direction.toUpperCase()), sortBy);
        return menuUseCase.getAllProducts(PageRequest.of(page, size, sort));
    }

    @GetMapping("/categories/{categoryId}/products")
    @Operation(summary = "Obtener productos por categoría con paginación y ordenamiento")
    public Page<MenuProductDto> getProductsByCategory(
            @PathVariable String categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "ASC") String direction) {
        
        Sort sort = Sort.by(Sort.Direction.fromString(direction.toUpperCase()), sortBy);
        return menuUseCase.getProductsByCategory(categoryId, PageRequest.of(page, size, sort));
    }

    @PostMapping("/products")
    @Operation(summary = "Crear o actualizar un producto")
    public ResponseEntity<MenuProductDto> createProduct(@Valid @RequestBody CreateMenuProductRequest request) {
        return new ResponseEntity<>(menuUseCase.saveProduct(request), HttpStatus.CREATED);
    }

    @PatchMapping("/products/{id}/status")
    @Operation(summary = "Actualizar estado activo/inactivo de un producto")
    public MenuProductDto updateProductStatus(@PathVariable String id, @RequestParam boolean active) {
        return menuUseCase.updateProductStatus(id, active);
    }

    @DeleteMapping("/products/{id}")
    @Operation(summary = "Eliminar un producto")
    public ResponseEntity<Void> deleteProduct(@PathVariable String id) {
        menuUseCase.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
