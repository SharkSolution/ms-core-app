package com.suresell.mscoreapp.infrastructure.persistence;

import com.suresell.mscoreapp.domain.model.ExpenseCategoryEntity;
import com.suresell.mscoreapp.domain.port.out.ExpenseCategoryRepository;
import com.suresell.mscoreapp.infrastructure.persistence.jpa.ExpenseCategoryJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ExpenseCategoryRepositoryImpl implements ExpenseCategoryRepository {

    private final ExpenseCategoryJpaRepository jpaRepository;

    public ExpenseCategoryRepositoryImpl(ExpenseCategoryJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public ExpenseCategoryEntity save(ExpenseCategoryEntity category) {
        return jpaRepository.save(category);
    }

    @Override
    public Optional<ExpenseCategoryEntity> findById(Long id) {
        return jpaRepository.findById(id);
    }

    @Override
    public Optional<ExpenseCategoryEntity> findByName(String name) {
        return jpaRepository.findByName(name);
    }

    @Override
    public List<ExpenseCategoryEntity> findAll() {
        return jpaRepository.findAll();
    }

    @Override
    public List<ExpenseCategoryEntity> findAllActive() {
        return jpaRepository.findByActiveTrue();
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return jpaRepository.existsById(id);
    }

    @Override
    public boolean existsByName(String name) {
        return jpaRepository.existsByName(name);
    }
}
