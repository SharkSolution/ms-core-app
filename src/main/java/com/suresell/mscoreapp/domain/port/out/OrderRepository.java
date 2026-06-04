package com.suresell.mscoreapp.domain.port.out;

import com.suresell.mscoreapp.domain.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Optional;

public interface OrderRepository {
    Page<Order> findAll(Specification<Order> spec, Pageable pageable);
    Optional<Order> findById(Long id);
    Order save(Order order);
}
