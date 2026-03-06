package com.suresell.mscoreapp.domain.port.out;

import com.suresell.mscoreapp.domain.model.OrderEditHistoryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface OrderEditHistoryRepository {
    Page<OrderEditHistoryEntity> findAll(Pageable pageable);
    Page<OrderEditHistoryEntity> findByOrderId(Long orderId, Pageable pageable);
    OrderEditHistoryEntity save(OrderEditHistoryEntity history);
}
