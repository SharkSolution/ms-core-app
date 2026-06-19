package com.suresell.mscoreapp.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "waiters")
public class WaiterEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "active")
    private Boolean active = true;

    // Meta de venta diaria del mesero, configurable desde el Admin (3.1). Nullable.
    @Column(name = "daily_sale_goal", precision = 12, scale = 2)
    private BigDecimal dailySaleGoal;
}
