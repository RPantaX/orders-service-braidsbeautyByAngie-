package com.braidsbeautyByAngie.entity;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "shop_order_entity")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShopOrderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "shop_order_id", nullable = false)
    private Long shopOrderId;
    @Column(name = "shop_order_date", nullable = false)
    private Timestamp shopOrderDate;
    @Column(name = "shop_order_total", nullable = false)
    private Double shopOrderTotal;
    @Column(name = "shop_order_status", nullable = false)
    private String shopOrderStatus;
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id", nullable = true)
    private AddressEntity addressEntity;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "shopping_method_id", nullable = false)
    private ShoppingMethodEntity shoppingMethodEntity;

    @OneToMany(mappedBy = "shopOrderEntity", cascade = CascadeType.ALL)
    private List<OrderLineEntity> orderLineEntities = new ArrayList<>();

    @Column(name = "modified_by_user", nullable = false, length = 15)
    private String modifiedByUser;

    @Column(name = "created_at", nullable = false)
    private Timestamp createdAt;

    @Column(name = "modified_at")
    private Timestamp modifiedAt;

    @Column(name = "deleted_at")
    private Timestamp deletedAt;
}
