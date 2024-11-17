package com.braidsbeautyByAngie.aggregates.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ShoppingMethodDTO {
    private Long shoppingMethodId;
    private String shoppingMethodName;
    private Double shoppingMethodPrice;
}
