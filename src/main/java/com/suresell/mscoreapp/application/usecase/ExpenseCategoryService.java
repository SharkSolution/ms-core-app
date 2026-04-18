package com.suresell.mscoreapp.application.usecase;

import com.suresell.mscoreapp.application.dto.CreateExpenseCategoryRequest;
import com.suresell.mscoreapp.application.dto.ExpenseCategoryDto;
import com.suresell.mscoreapp.domain.model.ExpenseCategoryEntity;
import com.suresell.mscoreapp.domain.port.out.ExpenseCategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ExpenseCategoryService {

    private final ExpenseCategoryRepository repository;
    private final ExpenseCategoryMapper mapper;

    public ExpenseCategoryService(ExpenseCategoryRepository repository, ExpenseCategoryMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public List<ExpenseCategoryDto> getAllCategories() {
        return mapper.toDtoList(repository.findAll());
    }

    public List<ExpenseCategoryDto> getActiveCategories() {
        return mapper.toDtoList(repository.findAllActive());
    }

    public ExpenseCategoryDto getCategoryById(Long id) {
        ExpenseCategoryEntity entity = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada con ID: " + id));
        return mapper.toDto(entity);
    }

    @Transactional
    public ExpenseCategoryDto createCategory(CreateExpenseCategoryRequest request) {
        if (request.getName() == null || request.getName().isBlank()) {
            throw new IllegalArgumentException("El nombre de la categoría es requerido");
        }

        if (repository.existsByName(request.getName())) {
            throw new IllegalArgumentException("Ya existe una categoría con el nombre: " + request.getName());
        }

        ExpenseCategoryEntity entity = mapper.toEntity(request);
        ExpenseCategoryEntity saved = repository.save(entity);
        return mapper.toDto(saved);
    }

    @Transactional
    public ExpenseCategoryDto updateCategory(Long id, CreateExpenseCategoryRequest request) {
        ExpenseCategoryEntity entity = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada con ID: " + id));

        if (request.getName() != null && !request.getName().equals(entity.getName())) {
            if (repository.existsByName(request.getName())) {
                throw new IllegalArgumentException("Ya existe una categoría con el nombre: " + request.getName());
            }
        }

        mapper.updateEntity(entity, request);
        ExpenseCategoryEntity saved = repository.save(entity);
        return mapper.toDto(saved);
    }

    @Transactional
    public void deactivateCategory(Long id) {
        ExpenseCategoryEntity entity = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada con ID: " + id));

        entity.setActive(false);
        repository.save(entity);
    }

    @Transactional
    public void activateCategory(Long id) {
        ExpenseCategoryEntity entity = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada con ID: " + id));

        entity.setActive(true);
        repository.save(entity);
    }
}
