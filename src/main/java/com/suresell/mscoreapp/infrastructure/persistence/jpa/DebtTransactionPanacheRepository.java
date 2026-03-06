package com.suresell.mscoreapp.infrastructure.persistence.jpa;

import com.suresell.mscoreapp.domain.model.DebtTransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DebtTransactionPanacheRepository extends JpaRepository<DebtTransactionEntity, String> {
    List<DebtTransactionEntity> findByAccountIdOrderByTransactionDateDesc(String accountId);
    List<DebtTransactionEntity> findByTransactionDateBetweenOrderByTransactionDateDesc(LocalDate start, LocalDate end);
    void deleteByAccountId(String accountId);
}
