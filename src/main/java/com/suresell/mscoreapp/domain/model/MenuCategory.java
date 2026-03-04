package com.suresell.mscoreapp.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "menu_categories")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MenuCategory {
    @Id
    @Column(name = "id_category", nullable = false, length = 255)
    private String idCategory;
    @Column(name = "name_category", nullable = false, length = 255)
    private String nameCategory;
    @OneToMany(mappedBy = "category")
    private List<MenuProduct> products = new ArrayList<>();
}
