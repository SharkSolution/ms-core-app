package com.suresell.mscoreapp.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
@Entity
@Table(name = "order_item")  
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  
    @Column(name = "id_order_item")
    private Long idOrderItem;
    @ManyToOne(fetch = FetchType.LAZY)  
    @JoinColumn(name = "order_id", nullable = false)  
    private Order order;
    @Column(name = "product_id")
    private String productId;
    private int quantity;
    @Column(name = "unit_price")
    private BigDecimal unitPrice;
    @Column(name = "total_price")
    private BigDecimal totalPrice;
    private String instructions;
    @Column(name = "combo_group")
    private Integer comboGroup;
}
