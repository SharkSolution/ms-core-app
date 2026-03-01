package com.suresell.mscoreapp.infrastructure.persistence.jpa;

import com.suresell.mscoreapp.domain.model.SupplyCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SupplyCategoryJpaRepository extends JpaRepository<SupplyCategory, Long> {
    Optional<SupplyCategory> findByName(String name);
}
