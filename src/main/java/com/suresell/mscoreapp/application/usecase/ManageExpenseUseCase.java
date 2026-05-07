package com.suresell.mscoreapp.application.usecase;

import com.suresell.mscoreapp.application.dto.ApproveRejectExpenseRequest;
import com.suresell.mscoreapp.application.dto.CreateExpenseRequest;
import com.suresell.mscoreapp.application.dto.ExpenseDto;
import com.suresell.mscoreapp.application.dto.ExpenseStatsResponse;
import com.suresell.mscoreapp.domain.model.ExpenseCategoryEntity;
import com.suresell.mscoreapp.domain.model.ExpenseEntity;
import com.suresell.mscoreapp.domain.port.out.ExpenseCategoryRepository;
import com.suresell.mscoreapp.domain.port.out.ExpenseRepository;
import com.suresell.mscoreapp.shared.enums.ExpenseStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ManageExpenseUseCase {

    private final ExpenseRepository expenseRepository;
    private final ExpenseCategoryRepository categoryRepository;
    private final ExpenseMapper mapper;

    public ManageExpenseUseCase(ExpenseRepository expenseRepository,
                                 ExpenseCategoryRepository categoryRepository,
                                 ExpenseMapper mapper) {
        this.expenseRepository = expenseRepository;
        this.categoryRepository = categoryRepository;
        this.mapper = mapper;
    }

    public List<ExpenseDto> getAllExpenses() {
        return mapper.toDtoList(expenseRepository.findAll());
    }

    public List<ExpenseDto> getExpensesByStatus(ExpenseStatus status) {
        return mapper.toDtoList(expenseRepository.findByStatus(status));
    }

    public List<ExpenseDto> getExpensesByCategory(Long categoryId) {
        return mapper.toDtoList(expenseRepository.findByCategoryId(categoryId));
    }

    public List<ExpenseDto> getExpensesByDateRange(LocalDate startDate, LocalDate endDate) {
        return mapper.toDtoList(expenseRepository.findByExpenseDateBetween(startDate, endDate));
    }

    public ExpenseDto getExpenseById(String id) {
        ExpenseEntity entity = expenseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Gasto no encontrado con ID: " + id));
        return mapper.toDto(entity);
    }

    @Transactional
    public ExpenseDto createExpense(CreateExpenseRequest request) {
        ExpenseCategoryEntity category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada con ID: " + request.getCategoryId()));

        if (!category.getActive()) {
            throw new IllegalArgumentException("La categoría está inactiva");
        }

        ExpenseEntity entity = mapper.toEntity(request);
        entity.setCategory(category);
        entity.setCode(expenseRepository.generateNextCode());
        entity.setStatus(ExpenseStatus.PENDING);

        ExpenseEntity saved = expenseRepository.save(entity);
        return mapper.toDto(saved);
    }

    @Transactional
    public ExpenseDto updateExpense(String id, CreateExpenseRequest request) {
        ExpenseEntity entity = expenseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Gasto no encontrado con ID: " + id));

        if (entity.getStatus() != ExpenseStatus.PENDING) {
            throw new IllegalArgumentException("Solo se pueden editar gastos en estado PENDIENTE");
        }

        if (request.getCategoryId() != null && !request.getCategoryId().equals(entity.getCategory().getId())) {
            ExpenseCategoryEntity category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada con ID: " + request.getCategoryId()));

            if (!category.getActive()) {
                throw new IllegalArgumentException("La categoría está inactiva");
            }
            entity.setCategory(category);
        }

        mapper.updateEntity(entity, request);
        ExpenseEntity saved = expenseRepository.save(entity);
        return mapper.toDto(saved);
    }

    @Transactional
    public ExpenseDto approveExpense(String id, ApproveRejectExpenseRequest request) {
        ExpenseEntity entity = expenseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Gasto no encontrado con ID: " + id));

        if (entity.getStatus() != ExpenseStatus.PENDING) {
            throw new IllegalArgumentException("Solo se pueden aprobar gastos en estado PENDIENTE");
        }

        entity.setStatus(ExpenseStatus.APPROVED);
        entity.setApprovedBy(request.getApprovedBy());
        entity.setApprovedAt(LocalDateTime.now());

        ExpenseEntity saved = expenseRepository.save(entity);
        return mapper.toDto(saved);
    }

    @Transactional
    public ExpenseDto rejectExpense(String id, ApproveRejectExpenseRequest request) {
        ExpenseEntity entity = expenseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Gasto no encontrado con ID: " + id));

        if (entity.getStatus() != ExpenseStatus.PENDING) {
            throw new IllegalArgumentException("Solo se pueden rechazar gastos en estado PENDIENTE");
        }

        if (request.getRejectionReason() == null || request.getRejectionReason().isBlank()) {
            throw new IllegalArgumentException("La razón de rechazo es requerida");
        }

        entity.setStatus(ExpenseStatus.REJECTED);
        entity.setApprovedBy(request.getApprovedBy());
        entity.setApprovedAt(LocalDateTime.now());
        entity.setRejectionReason(request.getRejectionReason());

        ExpenseEntity saved = expenseRepository.save(entity);
        return mapper.toDto(saved);
    }

    @Transactional
    public void deleteExpense(String id) {
        ExpenseEntity entity = expenseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Gasto no encontrado con ID: " + id));

        if (entity.getStatus() != ExpenseStatus.PENDING) {
            throw new IllegalArgumentException("Solo se pueden eliminar gastos en estado PENDIENTE");
        }

        expenseRepository.deleteById(id);
    }

    public ExpenseStatsResponse getExpenseStats(LocalDate startDate, LocalDate endDate) {
        List<ExpenseEntity> expenses;
        List<ExpenseEntity> pendingExpenses;
        List<ExpenseEntity> approvedExpenses;
        long rejectedCount;
        
        if (startDate != null && endDate != null) {
            expenses = expenseRepository.findByExpenseDateBetween(startDate, endDate);
            pendingExpenses = expenseRepository.findByStatusAndExpenseDateBetween(ExpenseStatus.PENDING, startDate, endDate);
            approvedExpenses = expenseRepository.findByStatusAndExpenseDateBetween(ExpenseStatus.APPROVED, startDate, endDate);
            rejectedCount = expenseRepository.findByStatusAndExpenseDateBetween(ExpenseStatus.REJECTED, startDate, endDate).size();
        } else {
            expenses = expenseRepository.findAll();
            pendingExpenses = expenseRepository.findByStatus(ExpenseStatus.PENDING);
            approvedExpenses = expenseRepository.findByStatus(ExpenseStatus.APPROVED);
            rejectedCount = expenseRepository.countByStatus(ExpenseStatus.REJECTED);
        }

        long totalExpenses = expenses.size();
        long pendingCount = pendingExpenses.size();
        long approvedCount = approvedExpenses.size();

        BigDecimal totalAmount = expenses.stream()
                .map(ExpenseEntity::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal approvedAmount = approvedExpenses.stream()
                .map(ExpenseEntity::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal pendingAmount = pendingExpenses.stream()
                .map(ExpenseEntity::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new ExpenseStatsResponse(
                totalExpenses,
                pendingCount,
                approvedCount,
                rejectedCount,
                totalAmount,
                approvedAmount,
                pendingAmount
        );
    }
}
