package com.suresell.mscoreapp.application.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApproveRejectExpenseRequest {
    @NotBlank(message = "El usuario aprobador es requerido")
    private String approvedBy;

    private String rejectionReason;
}
