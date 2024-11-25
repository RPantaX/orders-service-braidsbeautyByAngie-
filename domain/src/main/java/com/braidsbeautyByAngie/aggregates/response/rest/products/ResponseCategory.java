package com.braidsbeautyByAngie.aggregates.response.rest.products;

import lombok.*;

import java.util.List;
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ResponseCategory {
    private Long productCategoryId;
    private String productCategoryName;
    private List<PromotionDTO> promotionDTOList;
}
