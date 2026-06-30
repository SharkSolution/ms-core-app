package com.suresell.mscoreapp.domain.port.out;

import com.suresell.mscoreapp.domain.model.EmployeeEntity;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository {
    List<EmployeeEntity> findAll();
    List<EmployeeEntity> findByActive(boolean active);
    Optional<EmployeeEntity> findById(Long id);
    EmployeeEntity save(EmployeeEntity employee);
    void deleteById(Long id);
}
