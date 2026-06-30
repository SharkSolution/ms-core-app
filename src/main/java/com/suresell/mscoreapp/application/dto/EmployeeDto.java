package com.suresell.mscoreapp.application.dto;

import com.suresell.mscoreapp.shared.enums.PaymentMode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDto {
    private Long id;
    private String name;
    private String documentId;
    private String phone;
    private String role;
    private PaymentMode paymentMode;
    private BigDecimal baseSalary;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
