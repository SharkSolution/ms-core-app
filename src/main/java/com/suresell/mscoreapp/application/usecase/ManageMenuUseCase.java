package com.suresell.mscoreapp.application.usecase;

import com.suresell.mscoreapp.application.dto.*;
import com.suresell.mscoreapp.domain.model.MenuCategoryEntity;
import com.suresell.mscoreapp.domain.model.MenuProductEntity;
import com.suresell.mscoreapp.domain.port.out.MenuCategoryRepository;
import com.suresell.mscoreapp.domain.port.out.MenuProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ManageMenuUseCase {

    private final MenuCategoryRepository categoryRepository;
    private final MenuProductRepository productRepository;
    private final MenuCategoryMapper categoryMapper;
    private final MenuProductMapper productMapper;

    // --- Categorías ---

    @Transactional(readOnly = true)
    public Page<MenuCategoryDto> getAllCategories(Pageable pageable) {
        return categoryRepository.findAll(pageable).map(categoryMapper::toDto);
    }

    @Transactional
    public MenuCategoryDto saveCategory(CreateMenuCategoryRequest request) {
        MenuCategoryEntity entity = new MenuCategoryEntity();
        entity.setId(request.getId());
        entity.setName(request.getName());
        return categoryMapper.toDto(categoryRepository.save(entity));
    }

    @Transactional
    public void deleteCategory(String id) {
        if (!productRepository.findByCategoryId(id, Pageable.ofSize(1)).isEmpty()) {
            throw new IllegalStateException("No se puede eliminar una categoría que tiene productos asociados");
        }
        categoryRepository.deleteById(id);
    }

    // --- Productos ---

    @Transactional(readOnly = true)
    public Page<MenuProductDto> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable).map(productMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Page<MenuProductDto> getProductsByCategory(String categoryId, Pageable pageable) {
        return productRepository.findByCategoryId(categoryId, pageable).map(productMapper::toDto);
    }

    @Transactional
    public MenuProductDto saveProduct(CreateMenuProductRequest request) {
        MenuCategoryEntity category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada: " + request.getCategoryId()));

        MenuProductEntity entity = new MenuProductEntity();
        entity.setId(request.getId());
        entity.setName(request.getName());
        entity.setPrice(request.getPrice());
        entity.setActive(request.getActive());
        entity.setCategory(category);

        return productMapper.toDto(productRepository.save(entity));
    }

    @Transactional
    public MenuProductDto updateProductStatus(String id, boolean active) {
        MenuProductEntity entity = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado: " + id));
        entity.setActive(active);
        return productMapper.toDto(productRepository.save(entity));
    }

    @Transactional
    public void deleteProduct(String id) {
        productRepository.deleteById(id);
    }
}
