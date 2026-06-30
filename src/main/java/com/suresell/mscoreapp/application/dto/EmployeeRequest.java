package com.suresell.mscoreapp.application.dto;

import com.suresell.mscoreapp.shared.enums.PaymentMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Payload de creacion/edicion de un empleado de la maestra. Se reutiliza para POST y PUT.
 */
@Data
public class EmployeeRequest {

    @NotBlank(message = "El nombre del empleado es obligatorio")
    private String name;

    private String documentId;

    private String phone;

    @NotBlank(message = "El cargo del empleado es obligatorio")
    private String role;

    @NotNull(message = "La modalidad de pago es obligatoria")
    private PaymentMode paymentMode;

    private BigDecimal baseSalary;

    private Boolean active;
}
