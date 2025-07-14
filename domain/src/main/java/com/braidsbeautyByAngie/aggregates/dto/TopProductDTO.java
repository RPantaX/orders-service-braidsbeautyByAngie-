package com.braidsbeautyByAngie.aggregates.dto;

import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopProductDTO {
    private String productImage;
    private String productName;
    private String category;
    private BigDecimal price;
    private Long totalSold;
}