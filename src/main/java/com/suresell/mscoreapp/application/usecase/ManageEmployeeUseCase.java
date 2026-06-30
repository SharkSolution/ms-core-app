package com.suresell.mscoreapp.application.usecase;

import com.suresell.mscoreapp.application.dto.EmployeeDto;
import com.suresell.mscoreapp.application.dto.EmployeeRequest;
import com.suresell.mscoreapp.domain.model.EmployeeEntity;
import com.suresell.mscoreapp.domain.port.out.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ManageEmployeeUseCase {

    private final EmployeeRepository repository;
    private final EmployeeMapper mapper;

    @Transactional(readOnly = true)
    public List<EmployeeDto> getAllEmployees() {
        return mapper.toDtoList(repository.findAll());
    }

    @Transactional(readOnly = true)
    public List<EmployeeDto> getActiveEmployees() {
        return mapper.toDtoList(repository.findByActive(true));
    }

    @Transactional(readOnly = true)
    public EmployeeDto getEmployeeById(Long id) {
        return mapper.toDto(findOrThrow(id));
    }

    @Transactional
    public EmployeeDto createEmployee(EmployeeRequest request) {
        EmployeeEntity entity = new EmployeeEntity();
        applyRequest(entity, request);
        entity.setActive(request.getActive() == null ? Boolean.TRUE : request.getActive());
        return mapper.toDto(repository.save(entity));
    }

    @Transactional
    public EmployeeDto updateEmployee(Long id, EmployeeRequest request) {
        EmployeeEntity entity = findOrThrow(id);
        applyRequest(entity, request);
        if (request.getActive() != null) {
            entity.setActive(request.getActive());
        }
        return mapper.toDto(repository.save(entity));
    }

    @Transactional
    public EmployeeDto updateStatus(Long id, boolean active) {
        EmployeeEntity entity = findOrThrow(id);
        entity.setActive(active);
        return mapper.toDto(repository.save(entity));
    }

    @Transactional
    public void deleteEmployee(Long id) {
        repository.deleteById(id);
    }

    private void applyRequest(EmployeeEntity entity, EmployeeRequest request) {
        entity.setName(request.getName());
        entity.setDocumentId(request.getDocumentId());
        entity.setPhone(request.getPhone());
        entity.setRole(request.getRole());
        entity.setPaymentMode(request.getPaymentMode());
        entity.setBaseSalary(request.getBaseSalary() != null ? request.getBaseSalary() : BigDecimal.ZERO);
    }

    private EmployeeEntity findOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Empleado no encontrado: " + id));
    }
}
