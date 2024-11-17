package com.braidsbeautyByAngie.entity;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@Entity
@Table(name = "shopping_method_entity")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShoppingMethodEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "shopping_method_id", nullable = false)
    private Long shoppingMethodId;
    @Column(name = "shopping_method_name", nullable = false)
    private String shoppingMethodName;
    @Column(name = "shopping_method_price", nullable = false)
    private Double shoppingMethodPrice;

    @Column(name = "state", nullable = false)
    private Boolean state;

    @Column(name = "modified_by_user", nullable = false, length = 15)
    private String modifiedByUser;

    @Column(name = "created_at", nullable = false)
    private Timestamp createdAt;

    @Column(name = "modified_at")
    private Timestamp modifiedAt;

    @Column(name = "deleted_at")
    private Timestamp deletedAt;
}
