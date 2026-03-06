package com.suresell.mscoreapp.infrastructure.persistence;

import com.suresell.mscoreapp.domain.model.OrderEditHistoryEntity;
import com.suresell.mscoreapp.domain.port.out.OrderEditHistoryRepository;
import com.suresell.mscoreapp.infrastructure.persistence.jpa.OrderEditHistoryJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class OrderEditHistoryRepositoryImpl implements OrderEditHistoryRepository {

    private final OrderEditHistoryJpaRepository jpaRepository;

    @Override
    public Page<OrderEditHistoryEntity> findAll(Pageable pageable) {
        return jpaRepository.findAll(pageable);
    }

    @Override
    public Page<OrderEditHistoryEntity> findByOrderId(Long orderId, Pageable pageable) {
        return jpaRepository.findByOrderId(orderId, pageable);
    }

    @Override
    public OrderEditHistoryEntity save(OrderEditHistoryEntity history) {
        return jpaRepository.save(history);
    }
}
