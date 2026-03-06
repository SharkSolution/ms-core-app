package com.suresell.mscoreapp.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateMealPreparationRequest {
    private String mainDish;
    private String sideDish;
    private String soup;
    private String beverage;
    private String dessert;
    private Integer estimatedPortions;
    private String specialNotes;
}
