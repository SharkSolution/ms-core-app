package com.suresell.mscoreapp.infrastructure.persistence.jpa;

import com.suresell.mscoreapp.domain.model.DailyClosureEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DailyClosureJpaRepository extends JpaRepository<DailyClosureEntity, UUID> {
    List<DailyClosureEntity> findAllByOrderByClosingTimeDesc();
}
