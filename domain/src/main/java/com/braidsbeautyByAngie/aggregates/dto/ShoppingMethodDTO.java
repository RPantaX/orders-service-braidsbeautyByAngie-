package com.braidsbeautyByAngie.aggregates.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ShoppingMethodDTO {
    private Long shippingMethodId;
    private String shippingMethodName;
    private Double shippingMethodPrice;
}
