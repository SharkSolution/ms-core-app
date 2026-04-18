package com.suresell.mscoreapp.infrastructure.persistence.jpa;

import com.suresell.mscoreapp.domain.model.ExpenseCategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExpenseCategoryJpaRepository extends JpaRepository<ExpenseCategoryEntity, Long> {
    Optional<ExpenseCategoryEntity> findByName(String name);
    List<ExpenseCategoryEntity> findByActiveTrue();
    boolean existsByName(String name);
}
