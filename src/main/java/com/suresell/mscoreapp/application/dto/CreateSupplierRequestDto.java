package com.suresell.mscoreapp.application.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CreateSupplierRequestDto {
    private String source;       // KITCHEN | ADMIN (por defecto ADMIN)
    private Long supplierId;     // opcional
    private String createdBy;
    private String notes;
    private List<CreateSupplierRequestItemDto> items;
}
