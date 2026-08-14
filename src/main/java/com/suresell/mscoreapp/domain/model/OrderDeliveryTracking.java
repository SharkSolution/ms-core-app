package com.suresell.mscoreapp.domain.model;

import jakarta.persistence.*;
import lombok.*;
@Entity
@Table(name = "order_delivery_tracking")
@Data
@NoArgsConstructor
@AllArgsConstructor
// tenant_id no entra en equals: todas las filas de una sesion comparten negocio.
@EqualsAndHashCode(exclude = "order", callSuper = false)
public class OrderDeliveryTracking extends EntidadDeNegocio {
    @Id
    @Column(name = "order_id")
    private Long orderId;

    @Column(name = "order_id_uuid")
    private java.util.UUID orderIdUuid;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "order_id", referencedColumnName = "id_order")
    @ToString.Exclude
    private Order order;
        @Column(name = "delivered", nullable = false)
        private Boolean delivered = false;
    
        @Column(name = "pager_returned", nullable = false)
        private Boolean pagerReturned = false;
    
        @Column(name = "preparation_duration_seconds")
        private Integer preparationDurationSeconds;
    }
