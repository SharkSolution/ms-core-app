package com.suresell.mscoreapp.infrastructure.persistence.jpa;

import com.suresell.mscoreapp.domain.model.ExpenseEntity;
import com.suresell.mscoreapp.shared.enums.ExpenseStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ExpenseJpaRepository extends JpaRepository<ExpenseEntity, String> {
    Optional<ExpenseEntity> findByCode(String code);
    List<ExpenseEntity> findByStatus(ExpenseStatus status);
    List<ExpenseEntity> findByCategoryId(Long categoryId);
    List<ExpenseEntity> findByExpenseDateBetween(LocalDate startDate, LocalDate endDate);
    List<ExpenseEntity> findByStatusAndExpenseDateBetween(ExpenseStatus status, LocalDate startDate, LocalDate endDate);
    long countByStatus(ExpenseStatus status);

    @Query("SELECT COUNT(e) FROM ExpenseEntity e WHERE e.expenseDate = :today")
    long countByExpenseDate(@Param("today") LocalDate today);

    @Query("SELECT e.code FROM ExpenseEntity e WHERE e.code LIKE :prefix ORDER BY e.code DESC LIMIT 1")
    Optional<String> findLastCodeByPrefix(@Param("prefix") String prefix);
}
