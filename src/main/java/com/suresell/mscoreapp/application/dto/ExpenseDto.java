package com.suresell.mscoreapp.application.dto;

import com.suresell.mscoreapp.shared.enums.ExpenseStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExpenseDto {
    private String id;
    private String code;
    private BigDecimal amount;
    private Double quantity;
    private BigDecimal unitPrice;
    private String description;
    private String vendor;
    private String imageUrl;
    private Long categoryId;
    private String categoryName;
    private ExpenseStatus status;
    private LocalDate expenseDate;
    private String createdBy;
    private String approvedBy;
    private LocalDateTime approvedAt;
    private String rejectionReason;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
