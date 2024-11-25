package com.braidsbeautyByAngie.aggregates.response.rest.products;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ResponseProductItemDetail {
    private Long productItemId;
    private String productItemSKU;
    private int productItemQuantityInStock;
    private String productItemImage;
    private BigDecimal productItemPrice;
    private ResponseCategory responseCategory;
    private List<ResponseVariation> variations;
}
