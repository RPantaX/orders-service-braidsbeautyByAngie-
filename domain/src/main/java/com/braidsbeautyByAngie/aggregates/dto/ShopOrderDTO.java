package com.braidsbeautyByAngie.aggregates.dto;

import com.braidsbeautyByAngie.aggregates.types.ShopOrderStatusEnum;
import lombok.*;

import java.sql.Timestamp;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ShopOrderDTO {
    private Long shopOrderId;
    private Timestamp shopOrderDate;
    private Double shopOrderTotal;
    private ShopOrderStatusEnum shopOrderStatus;
    private Long userId;
    private Long paymentMethodId;

}
