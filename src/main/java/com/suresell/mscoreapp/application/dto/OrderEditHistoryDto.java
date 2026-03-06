package com.suresell.mscoreapp.application.dto;

import com.suresell.mscoreapp.shared.enums.OrderEditType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderEditHistoryDto {
    private Long id;
    private Long orderId;
    private OrderEditType editType;
    private String productId;
    private String productName;
    private Integer oldQuantity;
    private Integer newQuantity;
    private BigDecimal oldTotal;
    private BigDecimal newTotal;
    private LocalDateTime editedAt;
}
