package com.suresell.mscoreapp.infrastructure.persistence.jpa;

import com.suresell.mscoreapp.domain.model.SupplyConsumptionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SupplyConsumptionJpaRepository extends JpaRepository<SupplyConsumptionEntity, Long> {
    List<SupplyConsumptionEntity> findBySupplyId(Long supplyId);
}
