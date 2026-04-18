package com.suresell.mscoreapp.domain.port.out;

import com.suresell.mscoreapp.domain.model.ExpenseCategoryEntity;

import java.util.List;
import java.util.Optional;

public interface ExpenseCategoryRepository {
    ExpenseCategoryEntity save(ExpenseCategoryEntity category);
    Optional<ExpenseCategoryEntity> findById(Long id);
    Optional<ExpenseCategoryEntity> findByName(String name);
    List<ExpenseCategoryEntity> findAll();
    List<ExpenseCategoryEntity> findAllActive();
    void deleteById(Long id);
    boolean existsById(Long id);
    boolean existsByName(String name);
}
