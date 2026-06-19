package com.suresell.mscoreapp.application.dto;

import com.suresell.mscoreapp.shared.enums.PaymentMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class PayrollCreateRequest {
    @NotBlank
    private String employeeName;
    
    private String documentId;
    
    private String phone;
    
    @NotBlank
    private String role;
    
    @NotNull
    private PaymentMode paymentMode;
    
    private BigDecimal valuePerDay;
    
    private Double workedDays;
    
    private BigDecimal commissions;

    private BigDecimal bonuses;

    private BigDecimal overtimeValue;

    private String notes;
    
    private LocalDate paymentDate;
    
    @NotBlank
    private String createdBy;
}
