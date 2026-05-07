package com.suresell.mscoreapp.domain.repository;

import com.suresell.mscoreapp.domain.model.PayrollEntity;
import com.suresell.mscoreapp.shared.enums.PayrollStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PayrollRepository {
    PayrollEntity save(PayrollEntity payroll);
    Optional<PayrollEntity> findById(String id);
    Optional<PayrollEntity> findByCode(String code);
    List<PayrollEntity> findAll();
    List<PayrollEntity> findByStatus(PayrollStatus status);
    List<PayrollEntity> findByPaymentDateBetween(LocalDate startDate, LocalDate endDate);
    List<PayrollEntity> findByStatusAndPaymentDateBetween(PayrollStatus status, LocalDate startDate, LocalDate endDate);
    void deleteById(String id);
    String generateNextCode();
}
