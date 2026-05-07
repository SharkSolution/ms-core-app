package com.suresell.mscoreapp.application.mapper;

import com.suresell.mscoreapp.application.dto.PayrollCreateRequest;
import com.suresell.mscoreapp.application.dto.PayrollDto;
import com.suresell.mscoreapp.domain.model.PayrollEntity;
import org.springframework.stereotype.Component;

@Component
public class PayrollMapper {

    public PayrollEntity toEntity(PayrollCreateRequest request) {
        if (request == null) return null;
        
        PayrollEntity entity = new PayrollEntity();
        entity.setEmployeeName(request.getEmployeeName());
        entity.setDocumentId(request.getDocumentId());
        entity.setPhone(request.getPhone());
        entity.setRole(request.getRole());
        entity.setPaymentMode(request.getPaymentMode());
        entity.setValuePerDay(request.getValuePerDay());
        entity.setWorkedDays(request.getWorkedDays());
        entity.setCommissions(request.getCommissions());
        entity.setBonuses(request.getBonuses());
        entity.setNotes(request.getNotes());
        entity.setPaymentDate(request.getPaymentDate());
        entity.setCreatedBy(request.getCreatedBy());
        
        return entity;
    }

    public PayrollDto toDto(PayrollEntity entity) {
        if (entity == null) return null;
        
        PayrollDto dto = new PayrollDto();
        dto.setId(entity.getId());
        dto.setCode(entity.getCode());
        dto.setEmployeeName(entity.getEmployeeName());
        dto.setDocumentId(entity.getDocumentId());
        dto.setPhone(entity.getPhone());
        dto.setRole(entity.getRole());
        dto.setPaymentMode(entity.getPaymentMode());
        dto.setValuePerDay(entity.getValuePerDay());
        dto.setWorkedDays(entity.getWorkedDays());
        dto.setCommissions(entity.getCommissions());
        dto.setBonuses(entity.getBonuses());
        dto.setTotalValue(entity.getTotalValue());
        dto.setNotes(entity.getNotes());
        dto.setStatus(entity.getStatus());
        dto.setPaymentDate(entity.getPaymentDate());
        dto.setCreatedBy(entity.getCreatedBy());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        
        return dto;
    }
}
