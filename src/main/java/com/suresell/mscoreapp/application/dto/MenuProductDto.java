package com.suresell.mscoreapp.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MenuProductDto {
    private String id;
    private String name;
    private Integer price;
    private Boolean active;
    private String categoryId;
    private String categoryName;
}
