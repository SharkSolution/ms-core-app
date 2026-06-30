package com.suresell.mscoreapp.infrastructure.persistence;

import com.suresell.mscoreapp.domain.model.EmployeeEntity;
import com.suresell.mscoreapp.domain.port.out.EmployeeRepository;
import com.suresell.mscoreapp.infrastructure.persistence.jpa.EmployeeJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class EmployeeRepositoryImpl implements EmployeeRepository {

    private final EmployeeJpaRepository jpaRepository;

    @Override
    public List<EmployeeEntity> findAll() {
        return jpaRepository.findAll();
    }

    @Override
    public List<EmployeeEntity> findByActive(boolean active) {
        return jpaRepository.findByActive(active);
    }

    @Override
    public Optional<EmployeeEntity> findById(Long id) {
        return jpaRepository.findById(id);
    }

    @Override
    public EmployeeEntity save(EmployeeEntity employee) {
        return jpaRepository.save(employee);
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }
}
