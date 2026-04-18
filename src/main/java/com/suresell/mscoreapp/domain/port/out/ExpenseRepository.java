package com.suresell.mscoreapp.domain.port.out;

import com.suresell.mscoreapp.domain.model.ExpenseEntity;
import com.suresell.mscoreapp.shared.enums.ExpenseStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ExpenseRepository {
    ExpenseEntity save(ExpenseEntity expense);
    Optional<ExpenseEntity> findById(String id);
    Optional<ExpenseEntity> findByCode(String code);
    List<ExpenseEntity> findAll();
    List<ExpenseEntity> findByStatus(ExpenseStatus status);
    List<ExpenseEntity> findByCategoryId(Long categoryId);
    List<ExpenseEntity> findByExpenseDateBetween(LocalDate startDate, LocalDate endDate);
    List<ExpenseEntity> findByStatusAndExpenseDateBetween(ExpenseStatus status, LocalDate startDate, LocalDate endDate);
    void deleteById(String id);
    boolean existsById(String id);
    long countByStatus(ExpenseStatus status);
    long countTodayExpenses();
    String generateNextCode();
}
