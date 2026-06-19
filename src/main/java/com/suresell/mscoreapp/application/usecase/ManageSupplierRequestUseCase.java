package com.suresell.mscoreapp.application.usecase;

import com.suresell.mscoreapp.application.dto.CreateSupplierRequestDto;
import com.suresell.mscoreapp.application.dto.CreateSupplierRequestItemDto;
import com.suresell.mscoreapp.application.dto.SupplierRequestDto;
import com.suresell.mscoreapp.application.dto.SupplierRequestItemDto;
import com.suresell.mscoreapp.domain.model.Supplier;
import com.suresell.mscoreapp.domain.model.Supply;
import com.suresell.mscoreapp.domain.model.SupplierRequest;
import com.suresell.mscoreapp.domain.model.SupplierRequestItem;
import com.suresell.mscoreapp.domain.port.out.ISupplyRepository;
import com.suresell.mscoreapp.infrastructure.persistence.jpa.SupplierJpaRepository;
import com.suresell.mscoreapp.infrastructure.persistence.jpa.SupplierRequestJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Solicitudes de pedido a proveedor (1.3 / 4.1). Cocina o Admin crean la solicitud;
 * el Admin la revisa y cambia el estado al generar el pedido final.
 */
@Service
public class ManageSupplierRequestUseCase {

    private static final Set<String> VALID_STATUS = Set.of("PENDING", "REVIEWED", "ORDERED", "CANCELLED");

    private final SupplierRequestJpaRepository requestRepository;
    private final SupplierJpaRepository supplierRepository;
    private final ISupplyRepository supplyRepository;

    public ManageSupplierRequestUseCase(SupplierRequestJpaRepository requestRepository,
                                        SupplierJpaRepository supplierRepository,
                                        ISupplyRepository supplyRepository) {
        this.requestRepository = requestRepository;
        this.supplierRepository = supplierRepository;
        this.supplyRepository = supplyRepository;
    }

    @Transactional
    public SupplierRequestDto create(CreateSupplierRequestDto dto) {
        if (dto == null || dto.getItems() == null || dto.getItems().isEmpty()) {
            throw new IllegalArgumentException("Debe incluir al menos un producto en la solicitud");
        }

        SupplierRequest request = new SupplierRequest();
        request.setSource(dto.getSource());
        request.setSupplierId(dto.getSupplierId());
        request.setCreatedBy(dto.getCreatedBy());
        request.setNotes(dto.getNotes());

        for (CreateSupplierRequestItemDto itemDto : dto.getItems()) {
            if (itemDto.getQuantity() == null) {
                continue;
            }
            String name = itemDto.getProductName();
            if ((name == null || name.isBlank()) && itemDto.getSupplyId() != null) {
                name = supplyRepository.findById(itemDto.getSupplyId()).map(Supply::getName).orElse(null);
            }
            if (name == null || name.isBlank()) {
                throw new IllegalArgumentException("Cada producto requiere nombre o un supplyId válido");
            }
            SupplierRequestItem item = new SupplierRequestItem();
            item.setSupplyId(itemDto.getSupplyId());
            item.setProductName(name);
            item.setUnit(itemDto.getUnit());
            item.setQuantity(itemDto.getQuantity());
            request.addItem(item);
        }

        if (request.getItems().isEmpty()) {
            throw new IllegalArgumentException("Debe incluir al menos un producto con cantidad");
        }

        return toDto(requestRepository.save(request));
    }

    public List<SupplierRequestDto> list(String status) {
        List<SupplierRequest> rows = (status != null && !status.isBlank())
                ? requestRepository.findByStatusOrderByCreatedAtDesc(status.toUpperCase())
                : requestRepository.findAllByOrderByCreatedAtDesc();
        return rows.stream().map(this::toDto).collect(Collectors.toList());
    }

    public SupplierRequestDto getById(Long id) {
        return requestRepository.findById(id).map(this::toDto)
                .orElseThrow(() -> new IllegalArgumentException("Solicitud no encontrada"));
    }

    @Transactional
    public SupplierRequestDto updateStatus(Long id, String status) {
        if (status == null || !VALID_STATUS.contains(status.toUpperCase())) {
            throw new IllegalArgumentException("Estado inválido. Use: " + VALID_STATUS);
        }
        SupplierRequest request = requestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Solicitud no encontrada"));
        request.setStatus(status.toUpperCase());
        return toDto(requestRepository.save(request));
    }

    private SupplierRequestDto toDto(SupplierRequest r) {
        String supplierName = null;
        if (r.getSupplierId() != null) {
            supplierName = supplierRepository.findById(r.getSupplierId()).map(Supplier::getName).orElse(null);
        }
        List<SupplierRequestItemDto> items = r.getItems().stream()
                .map(i -> new SupplierRequestItemDto(
                        i.getId(), i.getSupplyId(), i.getProductName(), i.getUnit(), i.getQuantity()))
                .collect(Collectors.toList());
        return new SupplierRequestDto(
                r.getId(), r.getStatus(), r.getSource(), r.getSupplierId(), supplierName,
                r.getCreatedBy(), r.getNotes(), r.getCreatedAt(), r.getUpdatedAt(), items);
    }
}
