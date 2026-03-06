package com.suresell.mscoreapp.application.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SupplyCategoryDto {

    private Long id;
    private String name;

    public SupplyCategoryDto(Long id, String name) {
        this.id = id;
        this.name = name;
    }

}
