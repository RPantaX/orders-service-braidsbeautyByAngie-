package com.braidsbeautyByAngie.aggregates.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SalesAnalyticsDTO {
    private String period; // Jan, Feb, Week 1, etc.
    private Long productOrders;
    private Long serviceOrders;
    private Long totalOrders;
}