package com.suresell.mscoreapp.application.usecase;

import com.suresell.mscoreapp.application.dto.SupplierDto;
import com.suresell.mscoreapp.domain.model.Supplier;
import com.suresell.mscoreapp.infrastructure.persistence.jpa.SupplierJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/** CRUD mínimo de proveedores (1.3). */
@Service
public class SupplierService {

    private final SupplierJpaRepository supplierRepository;

    public SupplierService(SupplierJpaRepository supplierRepository) {
        this.supplierRepository = supplierRepository;
    }

    public List<SupplierDto> listActive() {
        return supplierRepository.findByActiveTrueOrderByNameAsc().stream()
                .map(this::toDto).collect(Collectors.toList());
    }

    @Transactional
    public SupplierDto create(SupplierDto dto) {
        if (dto.getName() == null || dto.getName().isBlank()) {
            throw new IllegalArgumentException("El nombre del proveedor es obligatorio");
        }
        Supplier supplier = new Supplier();
        supplier.setName(dto.getName());
        supplier.setPhone(dto.getPhone());
        supplier.setActive(dto.getActive() != null ? dto.getActive() : true);
        return toDto(supplierRepository.save(supplier));
    }

    private SupplierDto toDto(Supplier s) {
        return new SupplierDto(s.getId(), s.getName(), s.getPhone(), s.getActive());
    }
}
