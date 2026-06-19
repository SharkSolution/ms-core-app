package com.suresell.mscoreapp.infrastructure.persistence.jpa;

import com.suresell.mscoreapp.domain.model.WeeklyInventoryCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface WeeklyInventoryCountJpaRepository extends JpaRepository<WeeklyInventoryCount, Long> {

    List<WeeklyInventoryCount> findByWeekStartOrderByCreatedAtDesc(LocalDate weekStart);

    List<WeeklyInventoryCount> findAllByOrderByCreatedAtDesc();
}
