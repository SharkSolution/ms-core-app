package com.suresell.mscoreapp.infrastructure.persistence.jpa;

import com.suresell.mscoreapp.domain.model.OrderEditHistoryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderEditHistoryJpaRepository extends JpaRepository<OrderEditHistoryEntity, Long> {
    Page<OrderEditHistoryEntity> findByOrderId(Long orderId, Pageable pageable);
}
