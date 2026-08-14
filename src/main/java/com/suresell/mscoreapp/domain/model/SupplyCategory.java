package com.suresell.mscoreapp.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
// tenant_id no entra en equals: todas las filas de una sesion comparten negocio.
@EqualsAndHashCode(callSuper = false)
@Table(name = "supply_categories")
@AllArgsConstructor
@NoArgsConstructor
public class SupplyCategory extends EntidadDeNegocio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @OneToMany(mappedBy = "supplyCategory", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Supply> supplies = new ArrayList<>();

    public void addSupply(Supply supply) {
        supplies.add(supply);
        supply.setSupplyCategory(this);
    }
}
