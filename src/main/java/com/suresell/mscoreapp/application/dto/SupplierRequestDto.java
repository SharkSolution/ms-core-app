package com.suresell.mscoreapp.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupplierRequestDto {
    private Long id;
    private String status;
    private String source;
    private Long supplierId;
    private String supplierName;
    private String createdBy;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<SupplierRequestItemDto> items;
}
