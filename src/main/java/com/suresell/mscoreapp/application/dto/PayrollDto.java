package com.suresell.mscoreapp.application.dto;

import com.suresell.mscoreapp.shared.enums.PaymentMode;
import com.suresell.mscoreapp.shared.enums.PayrollStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class PayrollDto {
    private String id;
    private String code;
    private String employeeName;
    private String documentId;
    private String phone;
    private String role;
    private PaymentMode paymentMode;
    private BigDecimal valuePerDay;
    private Double workedDays;
    private BigDecimal commissions;
    private BigDecimal bonuses;
    private BigDecimal overtimeValue;
    private BigDecimal totalValue;
    private String notes;
    private PayrollStatus status;
    private LocalDate paymentDate;
    private String createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
