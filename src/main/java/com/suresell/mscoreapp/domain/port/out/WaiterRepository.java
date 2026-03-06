package com.suresell.mscoreapp.domain.port.out;

import com.suresell.mscoreapp.domain.model.WaiterEntity;

import java.util.List;
import java.util.Optional;

public interface WaiterRepository {
    List<WaiterEntity> findAll();
    List<WaiterEntity> findByActive(boolean active);
    Optional<WaiterEntity> findById(Long id);
    WaiterEntity save(WaiterEntity waiter);
    void deleteById(Long id);
}
