package com.suresell.mscoreapp.domain.model;

import com.suresell.mscoreapp.shared.enums.OrderEditType;
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
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "order_edit_history")
public class OrderEditHistoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @Enumerated(EnumType.STRING)
    @Column(name = "edit_type")
    private OrderEditType editType;

    @Column(name = "product_id")
    private String productId;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "old_quantity")
    private Integer oldQuantity;

    @Column(name = "new_quantity")
    private Integer newQuantity;

    @Column(name = "old_total", precision = 38, scale = 2)
    private BigDecimal oldTotal;

    @Column(name = "new_total", precision = 38, scale = 2)
    private BigDecimal newTotal;

    @Column(name = "edited_at")
    private LocalDateTime editedAt;

    @PrePersist
    public void prePersist() {
        if (this.editedAt == null) {
            this.editedAt = LocalDateTime.now();
        }
    }
}
