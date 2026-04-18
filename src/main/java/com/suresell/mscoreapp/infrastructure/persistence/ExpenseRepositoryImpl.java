package com.suresell.mscoreapp.infrastructure.persistence;

import com.suresell.mscoreapp.domain.model.ExpenseEntity;
import com.suresell.mscoreapp.domain.port.out.ExpenseRepository;
import com.suresell.mscoreapp.infrastructure.persistence.jpa.ExpenseJpaRepository;
import com.suresell.mscoreapp.shared.enums.ExpenseStatus;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Repository
public class ExpenseRepositoryImpl implements ExpenseRepository {

    private final ExpenseJpaRepository jpaRepository;

    public ExpenseRepositoryImpl(ExpenseJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public ExpenseEntity save(ExpenseEntity expense) {
        return jpaRepository.save(expense);
    }

    @Override
    public Optional<ExpenseEntity> findById(String id) {
        return jpaRepository.findById(id);
    }

    @Override
    public Optional<ExpenseEntity> findByCode(String code) {
        return jpaRepository.findByCode(code);
    }

    @Override
    public List<ExpenseEntity> findAll() {
        return jpaRepository.findAll();
    }

    @Override
    public List<ExpenseEntity> findByStatus(ExpenseStatus status) {
        return jpaRepository.findByStatus(status);
    }

    @Override
    public List<ExpenseEntity> findByCategoryId(Long categoryId) {
        return jpaRepository.findByCategoryId(categoryId);
    }

    @Override
    public List<ExpenseEntity> findByExpenseDateBetween(LocalDate startDate, LocalDate endDate) {
        return jpaRepository.findByExpenseDateBetween(startDate, endDate);
    }

    @Override
    public List<ExpenseEntity> findByStatusAndExpenseDateBetween(ExpenseStatus status, LocalDate startDate, LocalDate endDate) {
        return jpaRepository.findByStatusAndExpenseDateBetween(status, startDate, endDate);
    }

    @Override
    public void deleteById(String id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public boolean existsById(String id) {
        return jpaRepository.existsById(id);
    }

    @Override
    public long countByStatus(ExpenseStatus status) {
        return jpaRepository.countByStatus(status);
    }

    @Override
    public long countTodayExpenses() {
        return jpaRepository.countByExpenseDate(LocalDate.now());
    }

    @Override
    public String generateNextCode() {
        String datePrefix = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String prefix = "EXP-" + datePrefix + "-";

        Optional<String> lastCode = jpaRepository.findLastCodeByPrefix(prefix + "%");

        if (lastCode.isPresent()) {
            String lastSequence = lastCode.get().substring(prefix.length());
            int nextSequence = Integer.parseInt(lastSequence) + 1;
            return prefix + String.format("%04d", nextSequence);
        }

        return prefix + "0001";
    }
}
