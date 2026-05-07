package com.suresell.mscoreapp.infrastructure.persistence.jpa;

import com.suresell.mscoreapp.domain.model.PayrollEntity;
import com.suresell.mscoreapp.shared.enums.PayrollStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PayrollJpaRepository extends JpaRepository<PayrollEntity, String> {
    Optional<PayrollEntity> findByCode(String code);
    List<PayrollEntity> findByStatus(PayrollStatus status);
    List<PayrollEntity> findByPaymentDateBetween(LocalDate startDate, LocalDate endDate);
    List<PayrollEntity> findByStatusAndPaymentDateBetween(PayrollStatus status, LocalDate startDate, LocalDate endDate);

    @Query("SELECT p.code FROM PayrollEntity p WHERE p.code LIKE :prefix ORDER BY p.code DESC LIMIT 1")
    Optional<String> findLastCodeByPrefix(@Param("prefix") String prefix);
}
