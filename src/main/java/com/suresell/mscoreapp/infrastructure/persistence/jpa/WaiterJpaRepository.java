package com.suresell.mscoreapp.infrastructure.persistence.jpa;

import com.suresell.mscoreapp.domain.model.WaiterEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WaiterJpaRepository extends JpaRepository<WaiterEntity, Long> {
    List<WaiterEntity> findByActive(boolean active);
}
