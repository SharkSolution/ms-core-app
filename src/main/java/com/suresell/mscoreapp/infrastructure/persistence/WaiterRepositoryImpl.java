package com.suresell.mscoreapp.infrastructure.persistence;

import com.suresell.mscoreapp.domain.model.WaiterEntity;
import com.suresell.mscoreapp.domain.port.out.WaiterRepository;
import com.suresell.mscoreapp.infrastructure.persistence.jpa.WaiterJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class WaiterRepositoryImpl implements WaiterRepository {

    private final WaiterJpaRepository jpaRepository;

    @Override
    public List<WaiterEntity> findAll() {
        return jpaRepository.findAll();
    }

    @Override
    public List<WaiterEntity> findByActive(boolean active) {
        return jpaRepository.findByActive(active);
    }

    @Override
    public Optional<WaiterEntity> findById(Long id) {
        return jpaRepository.findById(id);
    }

    @Override
    public WaiterEntity save(WaiterEntity waiter) {
        return jpaRepository.save(waiter);
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }
}
