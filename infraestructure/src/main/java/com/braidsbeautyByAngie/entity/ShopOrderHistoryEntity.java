package com.braidsbeautyByAngie.entity;

import com.braidsbeautyByAngie.aggregates.types.ShopOrderHistoryStatusEnum;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@Entity
@Table(name = "shop_order_history")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShopOrderHistoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "shop_order_history_id", nullable = false)
    private Long shopOrderHistoryId;

    @Column(name = "shop_order_id", nullable = false)
    private Long shopOrderId;

    @Column(name = "status")
    private ShopOrderHistoryStatusEnum status;

    @Column(name = "created_at", nullable = false)
    private Timestamp createdAt;
}
