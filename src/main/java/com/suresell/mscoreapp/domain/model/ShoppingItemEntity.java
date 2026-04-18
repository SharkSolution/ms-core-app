package com.suresell.mscoreapp.domain.model;


import com.suresell.mscoreapp.shared.enums.ShoppingItemStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "shopping_items")
public class ShoppingItemEntity {
    @Id
    private String id;



    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "supply_category")
    private String supplyCategory;

    @Column(name = "unit")
    private String unit;

    @Column(name = "current_stock", precision = 10, scale = 2)
    private BigDecimal currentStock;

    @Column(name = "minimum_stock", precision = 10, scale = 2)
    private BigDecimal minimumStock;

    @Column(name = "suggested_quantity", precision = 10, scale = 2)
    private BigDecimal suggestedQuantity;

    @Column(name = "estimated_cost", precision = 10, scale = 2)
    private BigDecimal estimatedCost;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ShoppingItemStatus status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

}
