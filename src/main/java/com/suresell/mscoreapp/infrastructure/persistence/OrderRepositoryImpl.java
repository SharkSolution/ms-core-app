package com.suresell.mscoreapp.infrastructure.persistence;

import com.suresell.mscoreapp.domain.model.Order;
import com.suresell.mscoreapp.domain.port.out.OrderRepository;
import com.suresell.mscoreapp.infrastructure.persistence.jpa.OrderJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepository {

    private final OrderJpaRepository jpaRepository;

    @Override
    public Page<Order> findAll(Specification<Order> spec, Pageable pageable) {
        return jpaRepository.findAll(spec, pageable);
    }

    @Override
    public Optional<Order> findById(Long id) {
        return jpaRepository.findById(id);
    }

    @Override
    public Order save(Order order) {
        return jpaRepository.save(order);
    }
}
