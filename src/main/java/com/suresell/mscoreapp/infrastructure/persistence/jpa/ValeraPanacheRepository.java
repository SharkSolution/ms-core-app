package com.suresell.mscoreapp.infrastructure.persistence.jpa;

import com.suresell.mscoreapp.shared.enums.ValeraStatus;
import com.suresell.mscoreapp.shared.enums.ValeraType;
import com.suresell.mscoreapp.domain.model.ValeraEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ValeraPanacheRepository extends JpaRepository<ValeraEntity, String> {
    Optional<ValeraEntity> findByCode(String code);
    List<ValeraEntity> findByCustomerDocument(String customerDocument);
    List<ValeraEntity> findByStatus(ValeraStatus status);
    List<ValeraEntity> findByType(ValeraType type);
    List<ValeraEntity> findByStatusAndExpirationDateGreaterThanEqual(ValeraStatus status, LocalDate date);
    List<ValeraEntity> findByStatusAndExpirationDateBetween(ValeraStatus status, LocalDate start, LocalDate end);
    List<ValeraEntity> findByExpirationDateLessThanAndStatusNot(LocalDate date, ValeraStatus status);
    List<ValeraEntity> findByPurchaseDateBetweenOrderByPurchaseDateDesc(LocalDate start, LocalDate end);
    boolean existsByCode(String code);
    long countByStatus(ValeraStatus status);
    long countByCustomerDocument(String customerDocument);
}
