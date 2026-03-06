package com.suresell.mscoreapp.infrastructure.persistence.jpa;

import com.suresell.mscoreapp.shared.enums.AccountStatus;
import com.suresell.mscoreapp.domain.model.AccountReceivableEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AccountReceivablePanacheRepository extends JpaRepository<AccountReceivableEntity, String> {
    Optional<AccountReceivableEntity> findByCustomerDocument(String customerDocument);
    List<AccountReceivableEntity> findByStatus(AccountStatus status);
    List<AccountReceivableEntity> findByTotalDebtGreaterThanOrderByTotalDebtDesc(BigDecimal debt);
    List<AccountReceivableEntity> findByTotalDebtGreaterThanAndLastTransactionDateLessThanEqual(BigDecimal debt, LocalDate date);
    List<AccountReceivableEntity> findByTotalDebtBetweenOrderByTotalDebtDesc(BigDecimal min, BigDecimal max);
    List<AccountReceivableEntity> findByLastTransactionDateBetweenOrderByLastTransactionDateDesc(LocalDate start, LocalDate end);
    boolean existsByCustomerDocument(String customerDocument);
    long countByTotalDebtGreaterThan(BigDecimal debt);

    @Query("SELECT COALESCE(SUM(a.totalDebt), 0) FROM AccountReceivableEntity a WHERE a.totalDebt > 0")
    BigDecimal getTotalDebtAmount();

    @Query("SELECT COALESCE(AVG(a.totalDebt), 0) FROM AccountReceivableEntity a WHERE a.totalDebt > 0")
    BigDecimal getAverageDebtAmount();
}
