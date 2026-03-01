package com.suresell.mscoreapp.application.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UseMealRequest {
    @NotBlank(message = "Código de valera es requerido")
    private String valeraCode;

    private String usageNotes;
}
